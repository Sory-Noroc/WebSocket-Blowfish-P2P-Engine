package com.example.blowfish.connection

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.util.concurrent.ConcurrentLinkedQueue

object P2PConnectionManager {
    private val client = HttpClient(CIO) {
        install(WebSockets)
    }
    
    // Mesaje primite care așteaptă să fie citite de UI
    val incomingMessages = ConcurrentLinkedQueue<String>()
    
    // Sesiunea activă cu partenerul
    var activeSession: WebSocketSession? = null
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

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
                    
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            incomingMessages.add("Partner ($ip): $text")
                            println("Mesaj primit de la $ip: $text")
                        }
                    }
                    activeSession = null
                    println("Conexiune cu $ip închisă.")
                }
            } catch (e: Exception) {
                println("Eroare la conectarea cu $ip: ${e.message}")
            }
        }
    }

    /**
     * Trimite un mesaj pe sesiunea activă
     */
    suspend fun sendMessage(text: String) {
        activeSession?.send(Frame.Text(text))
    }
}