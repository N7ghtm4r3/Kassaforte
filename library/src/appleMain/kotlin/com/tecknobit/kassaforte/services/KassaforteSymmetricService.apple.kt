@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte.services

import com.tecknobit.kassaforte.Kassaforte
import com.tecknobit.kassaforte.key.genspec.BlockModeType
import com.tecknobit.kassaforte.key.genspec.BlockModeType.CTR
import com.tecknobit.kassaforte.key.genspec.BlockModeType.GCM
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyDetailsSheet
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyOperation.DECRYPT
import com.tecknobit.kassaforte.key.usages.KeyOperation.ENCRYPT
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.util.checkIfIsSupportedType
import korlibs.crypto.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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
        if (aliasExists(alias))
            throw RuntimeException(ALIAS_ALREADY_TAKEN_ERROR)
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
        storeKeyData(
            alias = alias,
            keyInfo = KeyInfo(
                keyPurposes = purposes,
                key = keyBytes
            )
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

    private fun storeKeyData(
        alias: String,
        keyInfo: KeyInfo,
    ) {
        val encodedKeyData = encodeKeyData(
            keyInfo = keyInfo
        )
        val kassaforte = Kassaforte(alias)
        kassaforte.safeguard(
            key = alias,
            data = encodedKeyData
        )
    }

    // TODO: TO ANNOTATE WITH @Returner
    private inline fun encodeKeyData(
        keyInfo: KeyInfo,
    ): String {
        val encodedKeyInfo = Json.encodeToString(keyInfo)
            .encodeToByteArray()
        return Base64.encode(encodedKeyInfo)
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
            keyOperation = ENCRYPT,
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
            keyOperation = DECRYPT,
            blockModeType = blockModeType,
            iv = iv,
            usage = { cipher -> cipher.decrypt(cipherText) }
        ).decodeToString()
    }

    private suspend inline fun useCipher(
        alias: String,
        keyOperation: KeyOperation,
        blockModeType: BlockModeType,
        iv: ByteArray,
        usage: (CipherWithModeAndPadding) -> ByteArray,
    ): ByteArray {
        // TODO: to remove when GCM integrated
        if (blockModeType == GCM)
            throw RuntimeException("GCM on iOs is currently missing, use CBC or CTR instead")
        val kassaforte = Kassaforte(alias)
        val encodedKeyData = kassaforte.withdraw(
            key = alias
        )
        if (encodedKeyData == null)
            throw RuntimeException(IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR)
        val decodedKeyData = Base64.decode(encodedKeyData)
            .decodeToString()
        val keyInfo: KeyInfo = Json.decodeFromString(decodedKeyData)
        if (!keyInfo.canPerform(keyOperation))
            throw RuntimeException(KEY_CANNOT_PERFORM_OPERATION_ERROR.replace("%s", keyOperation.name))
        // TODO: WHEN GCM AVAILABLE INTEGRATE IT
        val cipher = AES(keyInfo.key).get(
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

    @Serializable
    private data class KeyInfo(
        override val keyPurposes: KeyPurposes,
        override val key: ByteArray,
    ) : KeyDetailsSheet<ByteArray> {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is KeyInfo) return false

            if (keyPurposes != other.keyPurposes) return false
            if (!key.contentEquals(other.key)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = keyPurposes.hashCode()
            result = 31 * result + key.contentHashCode()
            return result
        }

    }

}