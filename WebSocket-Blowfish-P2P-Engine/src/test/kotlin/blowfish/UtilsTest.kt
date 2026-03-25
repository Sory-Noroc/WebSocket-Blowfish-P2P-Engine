package com.example.blowfish.blowfish

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class UtilsTest {
    private val dummyP = IntArray(18) { it + 1 }
    private val dummyS = Array(4) { IntArray(256) { it } }

    @Test
    fun `test f function logic`() {
        val x = 0x01020304

        val result = f(dummyS, x)

        assertEquals(4, result, "Functia F nu combina corect octetii!")
    }

    @Test
    fun `test block encrypt and decrypt symmetry`() {
        val originalLeft = 123456789
        val originalRight = 987654321

        val encryptedBlock = encrypt(dummyS, dummyP, originalLeft, originalRight)

        assertNotEquals(originalLeft, encryptedBlock.first)
        assertNotEquals(originalRight, encryptedBlock.second)

        val decryptedBlock = decrypt(dummyS, dummyP, encryptedBlock.first, encryptedBlock.second)

        assertEquals(originalLeft, decryptedBlock.first, "Partea stanga (Left) nu s-a decriptat corect!")
        assertEquals(originalRight, decryptedBlock.second, "Partea dreapta (Right) nu s-a decriptat corect!")
    }
}