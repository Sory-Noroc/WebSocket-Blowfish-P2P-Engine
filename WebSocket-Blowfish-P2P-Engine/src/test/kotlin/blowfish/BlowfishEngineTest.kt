package com.example.blowfish.blowfish

import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class BlowfishEngineTest {

    @Test
    fun `test encryption and decryption symmetry`() {
        val key = "SymmetricKey".toByteArray()
        val originalValue = Random.nextLong()
        val bfTester = BlowfishEngine(key)

        val encryptedValue = bfTester.encrypt(originalValue)

        assertNotEquals(originalValue, encryptedValue, "The encrypted value does not have to be identical to the original one!")

        val decryptedValue = bfTester.decrypt(encryptedValue)

        assertEquals(originalValue, decryptedValue, "Decryption must return the original value!")
    }

    @Test
    fun `test specific known value`() {
        val key = "SymmetricKey".toByteArray()
        val originalValue = 1234L
        val bfTester = BlowfishEngine(key)
        val encryptedValue = bfTester.encrypt(originalValue)
        val decryptedValue = bfTester.decrypt(encryptedValue)

        assertEquals(originalValue, decryptedValue)
    }
}