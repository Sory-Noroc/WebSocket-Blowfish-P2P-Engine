package blowfish

import com.example.blowfish.connection.DiffieHellman
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigInteger

class DiffieHellmanTest {

    @Test
    fun `Test shared secret agreement between two parties`() {
        val alice = DiffieHellman()
        val bob = DiffieHellman()

        val alicePubKey = alice.generatePublicKey()
        val bobPubKey = bob.generatePublicKey()

        assertNotEquals(alicePubKey, bobPubKey, "Cheile publice nu ar trebui să fie identice")

        val aliceSecret = alice.computeSharedSecret(bobPubKey)
        val bobSecret = bob.computeSharedSecret(alicePubKey)

        assertArrayEquals(aliceSecret, bobSecret) {
            "Secretul comun calculat de Alice nu coincide cu cel al lui Bob!"
        }

        println("Success: Secretele coincid!")
        println("Alice Secret (Hex): ${aliceSecret.joinToString("") { "%02x".format(it) }}")
    }

    @Test
    fun `Test that secrets are consistent across multiple runs`() {
        val dh = DiffieHellman()
        val dummyPubKey = BigInteger.valueOf(123456789)

        val secret1 = dh.computeSharedSecret(dummyPubKey)
        val secret2 = dh.computeSharedSecret(dummyPubKey)

        assertArrayEquals(secret1, secret2)
    }
}