package com.example.blowfish

import com.example.blowfish.connection.NetworkUtils
import com.example.blowfish.connection.P2PConnectionManager
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun Application.configureRouting() {
    routing {
        get("/ips") {
            val subnet: String = call.request.queryParameters["subnet"] ?: "0.0.0.0/0"
            log.info("Scanare subnet solicitată: $subnet")
            val ips = NetworkUtils.discoverPeers(subnet)
            call.respond(ips)
        }

        post("/connect") {
            val ip = call.receiveText()
            log.info("UI a solicitat conectarea la: $ip")
            P2PConnectionManager.connectToPeer(ip)
            call.respondText("Conectare inițiată către $ip")
        }

        post("/send") {
            val message = call.receiveText()
            P2PConnectionManager.sendMessage(message)
            call.respondText("Mesaj trimis")
        }

        get("/messages") {
            val messages = mutableListOf<String>()
            while (P2PConnectionManager.incomingMessages.isNotEmpty()) {
                messages.add(P2PConnectionManager.incomingMessages.poll())
            }
            call.respond(messages)
        }

        staticResources("/", "static", index = "index.html")
    }
}

fun Application.configureP2P() {
    routing {
        webSocket("/chat") {
            P2PConnectionManager.activeSession = this
            println("Un partener s-a conectat la noi.")
            
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        P2PConnectionManager.incomingMessages.add("Partner: $text")
                        println("Mesaj primit prin /chat: $text")
                    }
                }
            } catch (e: Exception) {
                println("Eroare în sesiunea /chat: ${e.message}")
            } finally {
                P2PConnectionManager.activeSession = null
                println("Partenerul s-a deconectat.")
            }
        }
    }
}