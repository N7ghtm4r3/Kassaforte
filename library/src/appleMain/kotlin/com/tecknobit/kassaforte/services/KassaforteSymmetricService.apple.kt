@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte.services

import com.tecknobit.kassaforte.Kassaforte
import com.tecknobit.kassaforte.key.KeyPurposes
import com.tecknobit.kassaforte.key.genspec.BlockModeType
import com.tecknobit.kassaforte.key.genspec.BlockModeType.CTR
import com.tecknobit.kassaforte.key.genspec.BlockModeType.GCM
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.util.checkIfIsSupportedType
import korlibs.crypto.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Security.SecRandomCopyBytes
import platform.Security.kSecRandomDefault
import kotlin.io.encoding.Base64

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteSymmetricService : KassaforteKeysService<SymmetricKeyGenSpec>() {

    actual override fun generateKey(
        alias: String,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        val keySize = keyGenSpec.keySize.bytes
        val keyBytes = ByteArray(keySize)
        val status = keyBytes.usePinned { pinned ->
            SecRandomCopyBytes(
                rnd = kSecRandomDefault,
                count = keySize.toULong(),
                bytes = pinned.addressOf(0)
            )
        }
        if (status != 0)
            throw RuntimeException("Error during the creation of the key")
        val kassaforte = Kassaforte(alias)
        kassaforte.safeguard(
            key = alias,
            data = Base64.encode(keyBytes)
        )
    }

    actual override fun aliasExists(
        alias: String
    ): Boolean {
        val kassaforte = Kassaforte(alias)
        return kassaforte.unsuspendedWithdraw(
            key = alias
        ) != null
    }

    actual suspend fun encrypt(
        alias: String,
        blockModeType: BlockModeType,
        paddingType: EncryptionPaddingType,
        data: Any,
    ): String {
        checkIfIsSupportedType(
            data = data
        )
        val iv = ByteArray(blockModeType.blockSize).apply {
            SecureRandom.nextBytes(this)
        }
        val encryptedData = useCipher(
            alias = alias,
            blockModeType = blockModeType,
            iv = iv,
            usage = { cipher ->
                val dataToEncrypt = data.toString().encodeToByteArray()
                cipher.encrypt(dataToEncrypt)
            }
        )
        return Base64.encode(iv + encryptedData)
    }

    actual suspend fun decrypt(
        alias: String,
        blockModeType: BlockModeType,
        paddingType: EncryptionPaddingType,
        data: String,
    ): String {
        val dataToDecrypt = Base64.decode(data)
        val blockSize = blockModeType.blockSize
        val iv = dataToDecrypt.copyOfRange(0, blockSize)
        val cipherText = dataToDecrypt.copyOfRange(blockSize, dataToDecrypt.size)
        return useCipher(
            alias = alias,
            blockModeType = blockModeType,
            iv = iv,
            usage = { cipher -> cipher.decrypt(cipherText) }
        ).decodeToString()
    }

    private suspend inline fun useCipher(
        alias: String,
        blockModeType: BlockModeType,
        iv: ByteArray,
        usage: (CipherWithModeAndPadding) -> ByteArray,
    ): ByteArray {
        if (blockModeType == GCM)
            TODO("GCM on iOs is currently missing, use CBC or CTR instead") // remove when GCM integrated
        val kassaforte = Kassaforte(alias)
        val encodedKey = kassaforte.withdraw(
            key = alias
        )
        if (encodedKey == null)
            throw RuntimeException(IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR)
        val decodedKey = Base64.decode(encodedKey)
        // TODO: WHEN GCM AVAILABLE INTEGRATE IT
        val cipher = AES(decodedKey).get(
            mode = when (blockModeType) {
                CTR -> CipherMode.CTR
                else -> CipherMode.CBC
            },
            padding = when (blockModeType) {
                CTR -> CipherPadding.NoPadding
                else -> CipherPadding.PKCS7Padding
            },
            iv = iv
        )
        return usage(cipher)
    }

    actual override fun deleteKey(
        alias: String
    ) {
        val kassaforte = Kassaforte(alias)
        kassaforte.remove(
            key = alias
        )
    }

}