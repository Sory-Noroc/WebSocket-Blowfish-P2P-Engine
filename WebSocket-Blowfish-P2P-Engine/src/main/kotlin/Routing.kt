package com.example.blowfish

import com.example.blowfish.blowfish.BlowfishEngine
import com.example.blowfish.blowfish.utils
import io.ktor.http.ContentType
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import kotlin.collections.flatten
import kotlin.time.Duration.Companion.seconds

fun Application.configureRouting() {
    routing {
        get("/") {
            val bfTester = BlowfishEngine()
            val encryption = bfTester.encrypt(4444)
            val decryption = bfTester.decrypt(encryption)
            val responsePayload = """
                App is UP!
                Encrypted: $encryption
                Decrypted: $decryption
            """.trimIndent()

            call.respondText(responsePayload)
        }
    }
}
