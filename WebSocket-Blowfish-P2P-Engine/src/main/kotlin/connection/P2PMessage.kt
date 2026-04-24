package connection

import kotlinx.serialization.Serializable

@Serializable
sealed class P2PMessage {
    @Serializable
    data class KeyExchange(val publicKeyHex: String) : P2PMessage()

    @Serializable
    data class EncryptedChat(val payloadHex: String) : P2PMessage()
}