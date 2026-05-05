package com.example.blowfish.connection

import kotlinx.serialization.Serializable

@Serializable
sealed class P2PMessage {
    @Serializable
    data class KeyExchange(val publicKeyHex: String) : P2PMessage()

    @Serializable
    data class EncryptedChat(val payloadHex: String) : P2PMessage()

    @Serializable
    data class DiscoveryRequest(val subnet: String) : P2PMessage()

    @Serializable
    data class DiscoveryResponse(val ips: List<String>) : P2PMessage()
}