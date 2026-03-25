package com.example.blowfish.blowfish

class BlowfishEngine {
    fun run() {
        var key_pos = 0
        var k = 0
        for (i in 0..17) {
            k = 0
            for (j in 0..3) {
                k = (k shl 8) or key[key_pos]
                key_pos = (key_pos + 1) % key.size
            }
            p[i] = p[i] xor k
        }

        var L: Int = 0
        var R: Int = 0
        for (i in 0..17 step 2) {
            val l_r_pair = encrypt(p, L, R)
            L = l_r_pair.first
            R = l_r_pair.second

            p[i] = L
            p[i + 1] = R
        }

        for (i in 0..3) {
            for (j in 0..255 step 2) {
                val l_r_pair = encrypt(p, L, R)
                L = l_r_pair.first
                R = l_r_pair.second

                s[i][j] = L
                s[i][j + 1] = L
            }
        }
    }
}