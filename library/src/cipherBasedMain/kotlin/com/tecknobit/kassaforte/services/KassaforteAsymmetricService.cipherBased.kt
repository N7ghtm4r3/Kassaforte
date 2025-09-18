package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.kassaforte.key.genspec.AlgorithmType
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.usages.KeyOperation.DECRYPT
import com.tecknobit.kassaforte.key.usages.KeyOperation.ENCRYPT
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.impls.KassaforteAsymmetricServiceImpl
import com.tecknobit.kassaforte.util.checkIfIsSupportedType
import javax.crypto.Cipher
import kotlin.io.encoding.Base64

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteAsymmetricService : KassaforteKeysService<AsymmetricKeyGenSpec>() {

    private val serviceImpl = KassaforteAsymmetricServiceImpl()

    actual override fun generateKey(
        algorithmType: AlgorithmType,
        alias: String,
        keyGenSpec: AsymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        serviceImpl.generateKey(
            algorithmType = algorithmType,
            alias = alias,
            keyGenSpec = keyGenSpec,
            purposes = purposes
        )
    }

    actual override fun aliasExists(
        alias: String,
    ): Boolean {
        return serviceImpl.aliasExists(
            alias = alias
        )
    }

    actual suspend fun encrypt(
        alias: String,
        paddingType: EncryptionPaddingType?,
        data: Any,
    ): String {
        checkIfIsSupportedType(
            data = data
        )
        val key = serviceImpl.getKey(
            alias = alias,
            keyOperation = ENCRYPT
        )
        val cipher = Cipher.getInstance(
            resolveTransformation(
                algorithm = key.algorithm,
                paddingType = paddingType
            )
        )
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val plainText = data.toString().encodeToByteArray()
        val cipherText = cipher.doFinal(plainText)
        return Base64.encode(cipherText)
    }

    actual suspend fun decrypt(
        alias: String,
        paddingType: EncryptionPaddingType?,
        data: String,
    ): String {
        val key = serviceImpl.getKey(
            alias = alias,
            keyOperation = DECRYPT
        )
        val cipher = Cipher.getInstance(
            resolveTransformation(
                algorithm = key.algorithm,
                paddingType = paddingType
            )
        )
        cipher.init(Cipher.DECRYPT_MODE, key)
        val cipherText = Base64.decode(data)
        val plainText = cipher.doFinal(cipherText)
        return plainText.decodeToString()
    }

    @Assembler
    private fun resolveTransformation(
        algorithm: String,
        paddingType: EncryptionPaddingType?,
    ): String {
        return serviceImpl.resolveTransformation(
            algorithm = algorithm,
            paddingType = paddingType
        )
    }

    actual override fun deleteKey(
        alias: String,
    ) {
        serviceImpl.deleteKey(
            alias = alias
        )
    }

}