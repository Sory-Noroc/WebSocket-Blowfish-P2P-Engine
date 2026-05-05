package com.example.blowfish

import com.example.blowfish.blowfish.utils
import com.example.blowfish.connection.NetworkUtils
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val subnet = "10.91.198.0/24"
    val discoveredIps = NetworkUtils.discoverPeers(subnet)

    if (discoveredIps.isNotEmpty()) {
        println("Am găsit parteneri la: $discoveredIps")

    } else {
        println("Niciun partener găsit. Aștept conexiuni...")
    }

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSockets()
    configureRouting()
    configureP2P()
}
