package com.example.blowfish.blowfish

object MessageProcessor {
    private const val BLOCK_SIZE = 8

    fun applyPadding(data: ByteArray): ByteArray {
        val paddingLength = BLOCK_SIZE - (data.size % BLOCK_SIZE)
        val paddedData = ByteArray(data.size + paddingLength)

        System.arraycopy(data, 0, paddedData, 0, data.size)

        val padValue = paddingLength.toByte()
        for (i in data.size until paddedData.size) {
            paddedData[i] = padValue
        }

        return paddedData
    }

    fun removePadding(data: ByteArray): ByteArray {
        if (data.isEmpty()) return data

        val paddingLength = data.last().toInt() and 0xFF

        if (paddingLength <= 0 || paddingLength > BLOCK_SIZE) {
            return data
        }

        return data.copyOfRange(0, data.size - paddingLength)
    }

    fun bytesToLongBlocks(data: ByteArray): List<Long> {
        val paddedData = applyPadding(data)
        val blocks = mutableListOf<Long>()

        for (i in paddedData.indices step BLOCK_SIZE) {
            var block = 0L
            for (j in 0 until BLOCK_SIZE) {
                block = (block shl 8) or (paddedData[i + j].toLong() and 0xFFL)
            }
            blocks.add(block)
        }
        return blocks
    }

    fun longBlocksToBytes(blocks: List<Long>): ByteArray {
        val rawBytes = ByteArray(blocks.size * BLOCK_SIZE)

        for (i in blocks.indices) {
            var block = blocks[i]
            for (j in BLOCK_SIZE - 1 downTo 0) {
                rawBytes[i * BLOCK_SIZE + j] = (block and 0xFFL).toByte()
                block = block ushr 8
            }
        }

        return removePadding(rawBytes)
    }
}