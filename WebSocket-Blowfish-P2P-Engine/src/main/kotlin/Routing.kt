package com.example.blowfish

import com.example.blowfish.connection.DiffieHellman
import com.example.blowfish.blowfish.BlowfishEngine
import com.example.blowfish.connection.NetworkUtils
import com.example.blowfish.connection.P2PMessage
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import java.math.BigInteger
import io.ktor.server.http.content.*
import io.ktor.server.response.*

fun Application.configureRouting() {
    routing {
        get("/ips") {
            val subnet: String = call.request.queryParameters["subnet"] ?: "0.0.0.0/0"
            log.info("Scanare subnet solicitată: $subnet")
            val ips = NetworkUtils.discoverPeers(subnet)
            call.respond(ips)
        }

        staticResources("/", "static", index = "index.html")
    }
}

fun Application.configureP2P() {
    routing {
        webSocket("/chat") {
            val dh = DiffieHellman()
            var blowfishEngine: BlowfishEngine? = null

            val myPublicKey = dh.generatePublicKey()
            send(Frame.Text(Json.encodeToString(P2PMessage.KeyExchange(myPublicKey.toString(16)))))

            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    val message = Json.decodeFromString<P2PMessage>(text)

                    when (message) {
                        is P2PMessage.KeyExchange -> {
                            val partnerPubKey = BigInteger(message.publicKeyHex, 16)
                            val sharedSecret = dh.computeSharedSecret(partnerPubKey)

                            blowfishEngine = BlowfishEngine(sharedSecret)
                            println("Handshake complet! Conexiune securizata stabilita.")
                        }

                        is P2PMessage.EncryptedChat -> {
                            val engine = blowfishEngine
                            if (engine != null) {
                                println("Mesaj criptat primit: ${message.payloadHex}")
                            }
                        }
                        else -> {
                            // Alte tipuri de mesaje P2P
                        }
                    }
                }
            }
        }
    }
}