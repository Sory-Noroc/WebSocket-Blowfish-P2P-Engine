package com.example.blowfish.connection

import java.math.BigInteger
import java.security.SecureRandom

class DiffieHellman {
    private val p = BigInteger(
        "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1" +
                "29024E088A67CC74020BBEA63B139B22514A08798E3404DD" +
                "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245" +
                "E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED" +
                "EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE65381" +
                "FFFFFFFFFFFFFFFF", 16
    )
    private val g = BigInteger.valueOf(2)

    private val privateKey: BigInteger = BigInteger(512, SecureRandom())

    fun generatePublicKey(): BigInteger {
        return g.modPow(privateKey, p)
    }

    fun computeSharedSecret(partnerPublicKey: BigInteger): ByteArray {
        val sharedSecret = partnerPublicKey.modPow(privateKey, p)

        val fullBytes = sharedSecret.toByteArray()

        return fullBytes.copyOfRange(0, minOf(fullBytes.size, 32))
    }
}