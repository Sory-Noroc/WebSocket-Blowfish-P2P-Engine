package com.example.blowfish.connection

import java.io.BufferedReader
import java.io.InputStreamReader

class NetworkUtils {
    companion object {
        fun discoverPeers(subnet: String): List<String> {
            val peers = mutableListOf<String>()

            try {
                val process = ProcessBuilder("nmap", "-p", "8080", "--open", subnet).start()
                val reader = BufferedReader(InputStreamReader(process.inputStream))

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    if (line!!.contains("Nmap scan report for")) {
                        val parts = line!!.split(" ")
                        val ip = parts.last().replace("(", "").replace(")", "")
                        peers.add(ip)
                        println("Partener găsit: $ip")
                    }
                }
                process.waitFor()
            } catch (e: Exception) {
                println("Eroare la execuția nmap: ${e.message}")
                println("Asigură-te că nmap este în PATH și poate fi executat.")
            }

            return peers.distinct()
        }
    }
}