package com.example.blowfish

import com.example.blowfish.connection.NetworkUtils
import com.example.blowfish.connection.P2PConnectionManager
import com.example.blowfish.connection.P2PStatus
import io.ktor.http.content.*
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

        get("/status") {
            val status = P2PStatus(
                myPublicKey = P2PConnectionManager.myPublicKey,
                partnerPublicKey = P2PConnectionManager.partnerPublicKey ?: "N/A",
                connected = (P2PConnectionManager.activeSession != null)
            )
            call.respond(status)
        }

        post("/send-file") {
            val multipart = call.receiveMultipart()
            var fileName = ""
            var fileBytes: ByteArray? = null

            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    fileName = part.originalFileName ?: "file"
                    fileBytes = part.streamProvider().readBytes()
                }
                part.dispose()
            }

            fileBytes?.let { bytes ->
                log.info("UI a trimis fisier pentru partener: $fileName")
                P2PConnectionManager.sendFile(fileName, bytes)
                call.respondText("Fisier trimis")
            } ?: run {
                call.respond(io.ktor.http.HttpStatusCode.BadRequest, "Niciun fisier selectat")
            }
        }

        get("/download") {
            val fileData = P2PConnectionManager.lastReceivedFileData
            val fileName = P2PConnectionManager.lastReceivedFileName

            if (fileData != null) {
                log.info("Descărcare solicitată pentru: $fileName")

                call.response.header(
                    io.ktor.http.HttpHeaders.ContentDisposition,
                    io.ktor.http.ContentDisposition.Attachment
                        .withParameter(io.ktor.http.ContentDisposition.Parameters.FileName, fileName)
                        .toString()
                )
                call.respondBytes(fileData)
            } else {
                call.respond(io.ktor.http.HttpStatusCode.NotFound, "Eroare: Niciun fișier primit recent.")
            }
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
                P2PConnectionManager.handleIncomingMessages(this)
            } catch (e: Exception) {
                println("Eroare în sesiunea /chat: ${e.message}")
            } finally {
                P2PConnectionManager.activeSession = null
                println("Partenerul s-a deconectat.")
            }
        }
    }
}
