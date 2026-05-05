package com.example.blowfish.connection

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.util.concurrent.ConcurrentLinkedQueue
import com.example.blowfish.blowfish.BlowfishEngine
import java.math.BigInteger

object P2PConnectionManager {
    private val client = HttpClient(CIO) {
        install(WebSockets)
    }
    
    private val json = Json { ignoreUnknownKeys = true }

    // Securitate
    private val dh = DiffieHellman()
    private var blowfish: BlowfishEngine? = null
    var myPublicKey: String = dh.generatePublicKey().toString(16)
    var partnerPublicKey: String? = null

    val incomingMessages = ConcurrentLinkedQueue<String>()

    var activeSession: WebSocketSession? = null
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var currentFileName: String? = null
    private var currentFileBytes = mutableListOf<Byte>()

    var lastReceivedFileData: ByteArray? = null
    var lastReceivedFileName: String = ""

    /**
     * Inițiază o conexiune către un alt calculator
     */
    fun connectToPeer(ip: String) {
        scope.launch {
            try {
                println("Încercare conectare la ws://$ip:8080/chat")
                client.webSocket(host = ip, port = 8080, path = "/chat") {
                    activeSession = this
                    println("Conectat la $ip!")

                    sendKeyExchange()
                    
                    handleIncomingMessages(this)
                    
                    activeSession = null
                    blowfish = null
                    println("Conexiune cu $ip închisă.")
                }
            } catch (e: Exception) {
                println("Eroare la conectarea cu $ip: ${e.message}")
            }
        }
    }

    private suspend fun sendKeyExchange() {
        val message: P2PMessage = P2PMessage.KeyExchange(myPublicKey)
        activeSession?.send(Frame.Text(json.encodeToString(message)))
    }

    suspend fun handleIncomingMessages(session: WebSocketSession) {
        for (frame in session.incoming) {
            if (frame is Frame.Text) {
                val text = frame.readText()
                try {
                    val message = json.decodeFromString<P2PMessage>(text)
                    when (message) {
                        is P2PMessage.KeyExchange -> {
                            partnerPublicKey = message.publicKeyHex
                            val sharedSecret = dh.computeSharedSecret(BigInteger(partnerPublicKey, 16))
                            blowfish = BlowfishEngine(sharedSecret)
                            
                            if (activeSession == null) {
                                activeSession = session
                                sendKeyExchange()
                            }
                        }
                        is P2PMessage.EncryptedChat -> {
                            val decrypted = if (blowfish != null) {
                                try {
                                    val blocks = message.payloadHex.split(",").map { it.toLong() }
                                    blowfish!!.decryptMessage(blocks, blowfish!!)
                                } catch (e: Exception) {
                                    "[Eroare Decriptare] ${message.payloadHex}"
                                }
                            } else {
                                "[Nesecurizat] ${message.payloadHex}"
                            }
                            incomingMessages.add(decrypted)
                        }
                        is P2PMessage.FileStart -> {
                            currentFileName = message.fileName
                            currentFileBytes.clear()
                        }
                        is P2PMessage.FileChunk -> {
                            val bytes = java.util.Base64.getDecoder().decode(message.dataHex)
                            currentFileBytes.addAll(bytes.toList())
                        }
                        is P2PMessage.FileEnd -> {
                            lastReceivedFileData = currentFileBytes.toByteArray()
                            lastReceivedFileName = message.fileName

                            incomingMessages.add("DOWNLOAD_READY:${message.fileName}")
                            currentFileName = null
                        }
                        else -> {}
                    }
                } catch (e: Exception) {
                    incomingMessages.add("Partner (Raw): $text")
                }
            }
        }
    }

    /**
     * Trimite un mesaj pe sesiunea activă
     */
    suspend fun sendMessage(text: String) {
        val payload = if (blowfish != null) {
            val encryptedBlocks = blowfish!!.encryptMessage(text, blowfish!!)
            encryptedBlocks.joinToString(",")
        } else {
            text
        }
        
        val message: P2PMessage = P2PMessage.EncryptedChat(payload)
        activeSession?.send(Frame.Text(json.encodeToString(message)))
    }

    suspend fun sendFile(fileName: String, bytes: ByteArray) {
        val session = activeSession ?: return
        
        session.send(Frame.Text(json.encodeToString<P2PMessage>(P2PMessage.FileStart(fileName, bytes.size.toLong()))))
        
        val chunkSize = 16384
        var offset = 0
        while (offset < bytes.size) {
            val end = minOf(offset + chunkSize, bytes.size)
            val chunk = bytes.copyOfRange(offset, end)
            val encodedChunk = java.util.Base64.getEncoder().encodeToString(chunk)
            session.send(Frame.Text(json.encodeToString<P2PMessage>(P2PMessage.FileChunk(encodedChunk))))
            offset = end
        }
        
        session.send(Frame.Text(json.encodeToString<P2PMessage>(P2PMessage.FileEnd(fileName))))
    }
}