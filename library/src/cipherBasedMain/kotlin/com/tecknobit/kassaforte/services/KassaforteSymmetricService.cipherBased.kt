package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.kassaforte.key.KeyPurposes
import com.tecknobit.kassaforte.key.genspec.BlockModeType
import com.tecknobit.kassaforte.key.genspec.BlockModeType.GCM
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.services.KeyOperation.DECRYPT
import com.tecknobit.kassaforte.services.KeyOperation.ENCRYPT
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import kotlin.io.encoding.Base64

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteSymmetricService : KassaforteKeysService<SymmetricKeyGenSpec>() {

    private val serviceImpl = KassaforteSymmetricServiceImpl()

    actual override fun generateKey(
        alias: String,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        serviceImpl.generateKey(
            alias = alias,
            keyGenSpec = keyGenSpec,
            purposes = purposes
        )
    }

    actual override fun aliasExists(
        alias: String
    ): Boolean {
        return serviceImpl.aliasExists(
            alias = alias
        )
    }

    actual suspend fun encrypt(
        alias: String,
        blockModeType: BlockModeType?,
        paddingType: EncryptionPaddingType?,
        data: Any,
    ): String {
        var cipherIv: ByteArray = byteArrayOf()
        var encryptedData = useCipher(
            alias = alias,
            blockModeType = blockModeType,
            paddingType = paddingType,
            keyOperation = ENCRYPT
        ) { cipher, key ->
            cipher.init(Cipher.ENCRYPT_MODE, key)
            cipherIv = cipher.iv
            val dataToEncrypt = data.toString().encodeToByteArray()
            cipher.doFinal(dataToEncrypt)
        }
        encryptedData = cipherIv + encryptedData
        return Base64.encode(encryptedData)
    }

    actual suspend fun decrypt(
        alias: String,
        blockModeType: BlockModeType?,
        paddingType: EncryptionPaddingType?,
        data: String,
    ): String {
        val decryptedData = useCipher(
            alias = alias,
            blockModeType = blockModeType,
            paddingType = paddingType,
            keyOperation = DECRYPT
        ) { cipher, key ->
            val dataToDecrypt = Base64.decode(data)
            val cypherText = when(blockModeType) {
                GCM -> {
                    val ivSeed = dataToDecrypt.copyOfRange(0, GCM_BLOCK_SIZE)
                    cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, ivSeed))
                    dataToDecrypt.copyOfRange(GCM_BLOCK_SIZE, dataToDecrypt.size)
                }
                else -> {
                    val iv = IvParameterSpec(dataToDecrypt.copyOfRange(0, CBC_CTR_BLOCK_SIZE))
                    cipher.init(Cipher.DECRYPT_MODE, key, iv)
                    dataToDecrypt.copyOfRange(CBC_CTR_BLOCK_SIZE, dataToDecrypt.size)
                }
            }
            cipher.doFinal(cypherText)
        }
        return decryptedData.decodeToString()
    }

    private inline fun useCipher(
        alias: String,
        blockModeType: BlockModeType?,
        paddingType: EncryptionPaddingType?,
        keyOperation: KeyOperation,
        cypherUsage: (Cipher, Key) -> ByteArray
    ): ByteArray {
        val key = serviceImpl.getKey(
            alias = alias,
            keyOperation = keyOperation
        )
        val transformation = resolveTransformation(
            algorithm = key.algorithm,
            blockModeType = blockModeType,
            paddingType = paddingType
        )
        val cipher = Cipher.getInstance(transformation)
        return cypherUsage(cipher, key)
    }

    // TODO: TO CHECK WHETHER MERGE
    @Assembler
    private fun resolveTransformation(
        algorithm: String,
        blockModeType: BlockModeType?,
        paddingType: EncryptionPaddingType?
    ): String {
        return serviceImpl.resolveTransformation(
            algorithm = algorithm,
            blockModeType = blockModeType,
            paddingType = paddingType
        )
    }

    actual override fun deleteKey(
        alias: String
    ) {
        serviceImpl.deleteKey(
            alias = alias
        )
    }

}