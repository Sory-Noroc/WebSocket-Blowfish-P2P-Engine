package com.example.blowfish.blowfish

import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class BlowfishEngineTest {

    @Test
    fun `test encryption and decryption symmetry`() {
        val originalValue = Random.nextLong()
        val bfTester = BlowfishEngine()

        val encryptedValue = bfTester.encrypt(originalValue)

        assertNotEquals(originalValue, encryptedValue, "Valoarea criptata nu trebuie sa fie identica cu cea originala!")

        val decryptedValue = bfTester.decrypt(encryptedValue)

        assertEquals(originalValue, decryptedValue, "Decriptarea trebuie sa returneze valoarea originala!")
    }

    @Test
    fun `test specific known value`() {
        val originalValue = 1234L
        val bfTester = BlowfishEngine()
        val encryptedValue = bfTester.encrypt(originalValue)
        val decryptedValue = bfTester.decrypt(encryptedValue)

        assertEquals(originalValue, decryptedValue)
    }
}