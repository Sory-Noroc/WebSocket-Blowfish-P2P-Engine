package com.example.blowfish.blowfish

/**
 * Encryption function for the BlowFish cipher
 * Takes 32-bit halves from the original 64-bit message and encrypts them over 16 rounds
 */
fun encrypt(p: Array<Int>, leftHalf: Int, rightHalf: Int): Pair<Int, Int> {
    var L = leftHalf
    var R = rightHalf

    for (round in 0 until 16) {
        L = L xor p[round]
        R = f(L) xor R
        // Swap L and R
        L = R.also { R = L }
    }
    L = R.also { R = L }
    R = R xor p[16]
    L = L xor p[17]

    return Pair(L, R)
}