package com.example.blowfish.connection

import java.io.BufferedReader
import java.io.InputStreamReader

class NetworkUtils {
    companion object {
        fun discoverPeers(subnet: String): List<String> {
            println("Se scanează rețeaua $subnet folosind nmap pentru portul 8080...")
            val peers = mutableListOf<String>()

            try {
                // nmap -p 8080 --open <subnet>
                // Exemplu subnet: 192.168.1.0/24
                val process = ProcessBuilder("nmap", "-p", "8080", "--open", subnet).start()
                val reader = BufferedReader(InputStreamReader(process.inputStream))

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    // Nmap scan report for 192.168.1.15
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