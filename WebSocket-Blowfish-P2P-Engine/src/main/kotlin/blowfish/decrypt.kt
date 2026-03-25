package com.example.blowfish.blowfish

fun decrypt(s: Array<Array<Int>>, p: Array<Int>, leftHalf: Int, rightHalf: Int) {
    var L = leftHalf
    var R = rightHalf
    for (round in 17..2) {
        L = L xor p[round]
        R = f(s, L) xor R
        L = R.also { R = L }
    }
    L = R.also { R = L }
    R = R xor p[1]
    L = L xor p[0]
}