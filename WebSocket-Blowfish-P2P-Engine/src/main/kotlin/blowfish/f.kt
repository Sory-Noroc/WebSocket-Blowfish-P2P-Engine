package com.example.blowfish.blowfish

fun f(s: Array<Array<Int>>, x: Int): Int {
    val highByte = (x ushr 24) and 0xff
    val secondByte = (x ushr 16) and 0xff
    val thirdByte = (x ushr 8) and 0xff
    val lowByte = x and 0xff

    val h = (s[0][highByte] + s[1][secondByte])
    return (h xor s[2][thirdByte]) + s[3][lowByte]
}