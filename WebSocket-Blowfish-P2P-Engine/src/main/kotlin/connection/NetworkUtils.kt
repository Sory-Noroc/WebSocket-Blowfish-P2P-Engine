package com.example.blowfish.connection

class NetworkUtils {
    companion object {
        fun discoverPeers(subnet: String): List<String> {
            println("Se scaneaza reteaua $subnet pentru parteneri...")
            val peers = mutableListOf<String>()

            try {
                val process = ProcessBuilder("nmap", "-p", "8080", "--open", subnet).start()
                val reader = process.inputStream.bufferedReader()

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    if (line!!.contains("Nmap scan report for")) {
                        val rawIp = line!!.substringAfter("for ").trim()
                        val ip = if (rawIp.contains("(")) rawIp.substringAfter("(").substringBefore(")") else rawIp
                        peers.add(ip)
                    }
                }
            } catch (e: Exception) {
                println("Eroare la scanare: Asigura-te că nmap este instalat!")
            }

            return peers
        }
    }
}