package blowfish

import com.example.blowfish.blowfish.BlowfishEngine
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer

class BlowfishVectorTest {
    private fun Long.toByteArray(): ByteArray {
        return ByteBuffer.allocate(8).putLong(this).array()
    }

    private fun assertBlowfishVector(keyLong: Long, plain: Long, expectedCipher: Long) {
        val keyBytes = keyLong.toByteArray()
        val engine = BlowfishEngine(keyBytes)

        val encrypted = engine.encrypt(plain)
        assertEquals(
            expectedCipher.toULong().toString(16).uppercase(),
            encrypted.toULong().toString(16).uppercase()
        ) {
            "Criptare esuata! Key: ${keyLong.toULong().toString(16)}, Plain: ${plain.toULong().toString(16)}"
        }

        val decrypted = engine.decrypt(encrypted)
        assertEquals(
            plain.toULong().toString(16).uppercase(),
            decrypted.toULong().toString(16).uppercase()
        ) {
            "Decriptare esuata! Key: ${keyLong.toULong().toString(16)}"
        }
    }

    @Test fun `Vector 1 - All Zeros`() = assertBlowfishVector(0x0000000000000000UL.toLong(), 0x0000000000000000UL.toLong(), 0x4EF997456198DD78UL.toLong())

    @Test fun `Vector 2 - All Fs`() = assertBlowfishVector(0xFFFFFFFFFFFFFFFFUL.toLong(), 0xFFFFFFFFFFFFFFFFUL.toLong(), 0x51866FD5B85ECB8AUL.toLong())

    @Test fun `Vector 3`() = assertBlowfishVector(0x3000000000000000UL.toLong(), 0x1000000000000001UL.toLong(), 0x7D856F9A613063F2UL.toLong())

    @Test fun `Vector 4`() = assertBlowfishVector(0x1111111111111111UL.toLong(), 0x1111111111111111UL.toLong(), 0x2466DD878B963C9DUL.toLong())

    @Test fun `Vector 5`() = assertBlowfishVector(0x0123456789ABCDEFUL.toLong(), 0x1111111111111111UL.toLong(), 0x61F9C3802281B096UL.toLong())

    @Test fun `Vector 6`() = assertBlowfishVector(0x1111111111111111UL.toLong(), 0x0123456789ABCDEFUL.toLong(), 0x7D0CC630AFDA1EC7UL.toLong())

    @Test fun `Vector 8 - FEDC Key`() = assertBlowfishVector(0xFEDCBA9876543210UL.toLong(), 0x0123456789ABCDEFUL.toLong(), 0x0ACEAB0FC6A0A28DUL.toLong())

    @Test fun `Vector 9`() = assertBlowfishVector(0x7CA110454A1A6E57UL.toLong(), 0x01A1D6D039776742UL.toLong(), 0x59C68245EB05282BUL.toLong())

    @Test fun `Vector 10`() = assertBlowfishVector(0x0131D9619DC1376EUL.toLong(), 0x5CD54CA83DEF57DAUL.toLong(), 0xB1B8CC0B250F09A0UL.toLong())

    @Test fun `Vector 11`() = assertBlowfishVector(0x07A1133E4A0B2686UL.toLong(), 0x0248D43806F67172UL.toLong(), 0x1730E5778BEA1DA4UL.toLong())

    @Test fun `Vector 12`() = assertBlowfishVector(0x3849674C2602319EUL.toLong(), 0x51454B582DDF440AUL.toLong(), 0xA25E7856CF2651EBUL.toLong())

    @Test fun `Vector 13`() = assertBlowfishVector(0x04B915BA43FEB5B6UL.toLong(), 0x42FD443059577FA2UL.toLong(), 0x353882B109CE8F1AUL.toLong())

    @Test fun `Vector 14`() = assertBlowfishVector(0x0113B970FD34F2CEUL.toLong(), 0x059B5E0851CF143AUL.toLong(), 0x48F4D0884C379918UL.toLong())

    @Test fun `Vector 15`() = assertBlowfishVector(0x0170F175468FB5E6UL.toLong(), 0x0756D8E0774761D2UL.toLong(), 0x432193B78951FC98UL.toLong())

    @Test fun `Vector 16`() = assertBlowfishVector(0x43297FAD38E373FEUL.toLong(), 0x762514B829BF486AUL.toLong(), 0x13F04154D69D1AE5UL.toLong())

    @Test fun `Vector 17`() = assertBlowfishVector(0x07A7137045DA2A16UL.toLong(), 0x3BDD119049372802UL.toLong(), 0x2EEDDA93FFD39C79UL.toLong())

    @Test fun `Vector 18`() = assertBlowfishVector(0x04689104C2FD3B2FUL.toLong(), 0x26955F6835AF609AUL.toLong(), 0xD887E0393C2DA6E3UL.toLong())

    @Test fun `Vector 19`() = assertBlowfishVector(0x37D06BB516CB7546UL.toLong(), 0x164D5E404F275232UL.toLong(), 0x5F99D04F5B163969UL.toLong())

    @Test fun `Vector 20`() = assertBlowfishVector(0x1F08260D1AC2465EUL.toLong(), 0x6B056E18759F5CCAUL.toLong(), 0x4A057A3B24D3977BUL.toLong())

    @Test fun `Vector 21`() = assertBlowfishVector(0x584023641ABA6176UL.toLong(), 0x004BD6EF09176062UL.toLong(), 0x452031C1E4FADA8EUL.toLong())

    @Test fun `Vector 22`() = assertBlowfishVector(0x025816164629B007UL.toLong(), 0x480D39006EE762F2UL.toLong(), 0x7555AE39F59B87BDUL.toLong())

    @Test fun `Vector 23`() = assertBlowfishVector(0x49793EBC79B3258FUL.toLong(), 0x437540C8698F3CFAUL.toLong(), 0x53C55F9CB49FC019UL.toLong())

    @Test fun `Vector 24`() = assertBlowfishVector(0x4FB05E1515AB73A7UL.toLong(), 0x072D43A077075292UL.toLong(), 0x7A8E7BFA937E89A3UL.toLong())

    @Test fun `Vector 25`() = assertBlowfishVector(0x49E95D6D4CA229BFUL.toLong(), 0x02FE55778117F12AUL.toLong(), 0xCF9C5D7A4986ADB5UL.toLong())

    @Test fun `Vector 26`() = assertBlowfishVector(0x018310DC409B26D6UL.toLong(), 0x1D9D5C5018F728C2UL.toLong(), 0xD1ABB290658BC778UL.toLong())

    @Test fun `Vector 27`() = assertBlowfishVector(0x1C587F1C13924FEFUL.toLong(), 0x305532286D6F295AUL.toLong(), 0x55CB3774D13EF201UL.toLong())

    @Test fun `Vector 28 - Step Sequence`() = assertBlowfishVector(0x0101010101010101UL.toLong(), 0x0123456789ABCDEFUL.toLong(), 0xFA34EC4847B268B2UL.toLong())

    @Test fun `Vector 29`() = assertBlowfishVector(0x1F1F1F1F0E0E0E0EUL.toLong(), 0x0123456789ABCDEFUL.toLong(), 0xA790795108EA3CAEUL.toLong())

    @Test fun `Vector 30`() = assertBlowfishVector(0xE0FEE0FEF1FEF1FEUL.toLong(), 0x0123456789ABCDEFUL.toLong(), 0xC39E072D9FAC631DUL.toLong())

    @Test fun `Vector 31`() = assertBlowfishVector(0x0000000000000000UL.toLong(), 0xFFFFFFFFFFFFFFFFUL.toLong(), 0x014933E0CDAFF6E4UL.toLong())

    @Test fun `Vector 32`() = assertBlowfishVector(0xFFFFFFFFFFFFFFFFUL.toLong(), 0x0000000000000000UL.toLong(), 0xF21E9A77B71C49BCUL.toLong())

    @Test fun `Vector 33`() = assertBlowfishVector(0x0123456789ABCDEFUL.toLong(), 0x0000000000000000UL.toLong(), 0x245946885754369AUL.toLong())

    @Test fun `Vector 34`() = assertBlowfishVector(0xFEDCBA9876543210UL.toLong(), 0xFFFFFFFFFFFFFFFFUL.toLong(), 0x6B5C5A9C5D9E0A5AUL.toLong())
}