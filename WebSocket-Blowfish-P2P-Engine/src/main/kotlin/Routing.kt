package com.example.blowfish

import com.example.blowfish.blowfish.BlowfishEngine
import com.example.blowfish.blowfish.MessageProcessor
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
            val key = "MySecretKey".toByteArray()
            val bfTester = BlowfishEngine(key)

            val plaintext = "Hello! This is a very long message for verifying Kotlin padding."

            val encryptedBlocks = bfTester.encryptMessage(plaintext, bfTester)

            val decryptedText = bfTester.decryptMessage(encryptedBlocks, bfTester)

            val responsePayload = """
                App is UP!
                Original: $plaintext
                Encrypted Blocks: ${encryptedBlocks.joinToString(", ") { it.toULong().toString(16) }}
                Decrypted: $decryptedText
            """.trimIndent()

            call.respondText(responsePayload)
        }
    }
}
