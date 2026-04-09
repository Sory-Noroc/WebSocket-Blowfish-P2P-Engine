package com.example.blowfish.blowfish

import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class BlowfishEngineTest {

    @Test
    fun `test encryption and decryption symmetry`() {
        val originalValue = Random.nextLong()
        val key = 8970_2354L
        val bfTester = BlowfishEngine(key)

        val encryptedValue = bfTester.encrypt(originalValue)

        assertNotEquals(originalValue, encryptedValue, "Valoarea criptata nu trebuie sa fie identica cu cea originala!")

        val decryptedValue = bfTester.decrypt(encryptedValue)

        assertEquals(originalValue, decryptedValue, "Decriptarea trebuie sa returneze valoarea originala!")
    }

    @Test
    fun `test specific known value`() {
        val key = 3644_6563L
        val originalValue = 1234L
        val bfTester = BlowfishEngine(key)
        val encryptedValue = bfTester.encrypt(originalValue)
        val decryptedValue = bfTester.decrypt(encryptedValue)

        assertEquals(originalValue, decryptedValue)
    }

    @Test
    fun `test vectors implementations`() {
        val keys = arrayOf(0L, )
        val plaintexts = arrayOf(0L, )
        val ciphertext = arrayOf(0x4EF9974561D8D288L, )

        for (i in keys.indices) {
            val engine = BlowfishEngine(keys[i])
            val encryption = engine.encrypt(plaintexts[i])
            val decryption = engine.decrypt(encryption)
            assertEquals(encryption, ciphertext[i], "The ciphertext does not match!")
            assertEquals(decryption, plaintexts[i], "The decryption algorithm is wrong!")
        }
    }
}