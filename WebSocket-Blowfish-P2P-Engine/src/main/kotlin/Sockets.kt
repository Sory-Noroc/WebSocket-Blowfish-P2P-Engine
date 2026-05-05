package com.example.blowfish

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import kotlin.time.Duration.Companion.seconds

import com.example.blowfish.connection.NetworkUtils
import com.example.blowfish.connection.P2PMessage
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/ws") {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    try {
                        val message = Json.decodeFromString<P2PMessage>(text)
                        when (message) {
                            is P2PMessage.DiscoveryRequest -> {
                                val ips = NetworkUtils.discoverPeers(message.subnet)
                                val response = P2PMessage.DiscoveryResponse(ips)
                                outgoing.send(Frame.Text(Json.encodeToString(response)))
                            }
                            else -> {
                                outgoing.send(Frame.Text("Echo: $text"))
                            }
                        }
                    } catch (e: Exception) {
                        // Fallback for non-JSON messages (like simple text for testing)
                        outgoing.send(Frame.Text("Server: $text"))
                    }
                }
            }
        }
    }
}
