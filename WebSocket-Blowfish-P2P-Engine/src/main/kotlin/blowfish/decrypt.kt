package com.example.blowfish.blowfish

fun decrypt(s: Array<IntArray>, p: IntArray, leftHalf: Int, rightHalf: Int): Pair<Int, Int> {
    var L = leftHalf
    var R = rightHalf
    for (round in 17 downTo 2) {
        L = L xor p[round]
        R = f(s, L) xor R
        L = R.also { R = L }
    }
    L = R.also { R = L }
    R = R xor p[1]
    L = L xor p[0]

    return Pair(L, R)
}