package com.example.blowfish

import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryTest {

    @Test
    fun testSHL() {
        val b = 1L
        val shifted = b.shl(3)
        assertEquals(8, shifted)
    }
}