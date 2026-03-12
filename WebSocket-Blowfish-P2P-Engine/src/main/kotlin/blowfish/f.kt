package com.example.blowfish.blowfish

fun f(s: Array<Array<Int>>, x: Int): Int {
    val highByte : Int = x shr 24
    val secondByte: Int = (x shr 16) and 0xff
    val thirdByte: Int = (x shr 8) and 0xff
    val lowByte: Int = x and 0xff

    val h = s[0][highByte] + s[1][secondByte]
    return (h xor s[2][thirdByte]) + s[3][lowByte]
}