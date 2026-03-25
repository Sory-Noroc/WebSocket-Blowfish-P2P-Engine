package com.example.blowfish

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
            val p_s: Pair<IntArray, Array<IntArray>> = utils.extractBlowfishConstants(utils.pi_hex)
            val sBoxesString = p_s.second.joinToString(
                separator = ",\n",
                prefix = "val s = arrayOf(\n",
                postfix = "\n)"
            ) { singleSBox ->
                singleSBox.joinToString(
                    separator = ", ",
                    prefix = "    intArrayOf(",
                    postfix = ")"
                ) { value ->
                    String.format("0x%08x.toInt()", value)
                }
            }
            call.respondText(sBoxesString)
        }
    }
}
