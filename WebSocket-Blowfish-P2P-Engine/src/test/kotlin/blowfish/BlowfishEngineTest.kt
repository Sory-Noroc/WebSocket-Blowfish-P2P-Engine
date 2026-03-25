package com.example.blowfish.blowfish

import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class BlowfishEngineTest {

    @Test
    fun `test encryption and decryption symmetry`() {
        val originalValue = Random.nextLong()

        val encryptedValue = BlowfishEngine.encrypt(originalValue)

        assertNotEquals(originalValue, encryptedValue, "Valoarea criptata nu trebuie sa fie identica cu cea originala!")

        val decryptedValue = BlowfishEngine.decrypt(encryptedValue)

        assertEquals(originalValue, decryptedValue, "Decriptarea trebuie sa returneze valoarea originala!")
    }

    @Test
    fun `test specific known value`() {
        val originalValue = 4444L
        val encryptedValue = BlowfishEngine.encrypt(originalValue)
        val decryptedValue = BlowfishEngine.decrypt(encryptedValue)

        assertEquals(originalValue, decryptedValue)
    }
}