package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.BlockMode
import com.tecknobit.kassaforte.key.genspec.BlockMode.GCM
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyOperation.DECRYPT
import com.tecknobit.kassaforte.key.usages.KeyOperation.ENCRYPT
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.impls.KassaforteSymmetricServiceImpl
import com.tecknobit.kassaforte.util.checkIfIsSupportedType
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import kotlin.io.encoding.Base64

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteSymmetricService : KassaforteKeysService<SymmetricKeyGenSpec>() {

    private val serviceImpl = KassaforteSymmetricServiceImpl()

    actual override fun generateKey(
        algorithm: Algorithm,
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
        blockMode: BlockMode,
        padding: EncryptionPadding,
        data: Any,
    ): String {
        checkIfIsSupportedType(
            data = data
        )
        var cipherIv: ByteArray = byteArrayOf()
        var encryptedData = useCipher(
            alias = alias,
            blockMode = blockMode,
            padding = padding,
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
        blockMode: BlockMode,
        padding: EncryptionPadding,
        data: String,
    ): String {
        val decryptedData = useCipher(
            alias = alias,
            blockMode = blockMode,
            padding = padding,
            keyOperation = DECRYPT
        ) { cipher, key ->
            val dataToDecrypt = Base64.decode(data)
            val blockSize = blockMode.blockSize
            val ivSeed = dataToDecrypt.copyOfRange(0, blockSize)
            val algorithmParameterSpec = when (blockMode) {
                GCM -> GCMParameterSpec(128, ivSeed)
                else -> IvParameterSpec(ivSeed)
            }
            cipher.init(Cipher.DECRYPT_MODE, key, algorithmParameterSpec)
            val cipherText = dataToDecrypt.copyOfRange(blockSize, dataToDecrypt.size)
            cipher.doFinal(cipherText)
        }
        return decryptedData.decodeToString()
    }

    private inline fun useCipher(
        alias: String,
        blockMode: BlockMode?,
        padding: EncryptionPadding?,
        keyOperation: KeyOperation,
        cypherUsage: (Cipher, Key) -> ByteArray,
    ): ByteArray {
        val key = serviceImpl.getKey(
            alias = alias,
            keyOperation = keyOperation
        )
        val transformation = resolveTransformation(
            algorithm = key.algorithm,
            blockMode = blockMode,
            padding = padding
        )
        val cipher = Cipher.getInstance(transformation)
        return cypherUsage(cipher, key)
    }

    @Assembler
    private fun resolveTransformation(
        algorithm: String,
        blockMode: BlockMode?,
        padding: EncryptionPadding?,
    ): String {
        return serviceImpl.resolveTransformation(
            algorithm = algorithm,
            blockMode = blockMode,
            padding = padding
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