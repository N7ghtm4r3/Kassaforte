package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.kassaforte.key.genspec.AlgorithmType
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.DigestType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType.RSA_OAEP
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType.RSA_PKCS1
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyOperation.DECRYPT
import com.tecknobit.kassaforte.key.usages.KeyOperation.ENCRYPT
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.OAEPWith.Companion.oaepWithValue
import com.tecknobit.kassaforte.services.impls.KassaforteAsymmetricServiceImpl
import com.tecknobit.kassaforte.util.checkIfIsSupportedCipherAlgorithm
import com.tecknobit.kassaforte.util.checkIfIsSupportedType
import java.security.Key
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
        digestType: DigestType?,
        data: Any,
    ): String {
        checkIfIsSupportedType(
            data = data
        )
        val cipherText = useCipher(
            alias = alias,
            keyOperation = ENCRYPT,
            paddingType = paddingType,
            digestType = digestType,
            usage = { cipher, key ->
                cipher.init(Cipher.ENCRYPT_MODE, key)
                val plainText = data.toString().encodeToByteArray()
                cipher.doFinal(plainText)
            }
        )
        return Base64.encode(cipherText)
    }

    actual suspend fun decrypt(
        alias: String,
        paddingType: EncryptionPaddingType?,
        digestType: DigestType?,
        data: String,
    ): String {
        val plainText = useCipher(
            alias = alias,
            keyOperation = DECRYPT,
            paddingType = paddingType,
            digestType = digestType,
            usage = { cipher, key ->
                cipher.init(Cipher.DECRYPT_MODE, key)
                val cipherText = Base64.decode(data)
                cipher.doFinal(cipherText)
            }
        )
        return plainText.decodeToString()
    }

    private inline fun useCipher(
        alias: String,
        keyOperation: KeyOperation,
        paddingType: EncryptionPaddingType?,
        digestType: DigestType?,
        usage: (Cipher, Key) -> ByteArray,
    ): ByteArray {
        val key = serviceImpl.getKey(
            alias = alias,
            keyOperation = keyOperation
        )
        val algorithm = key.algorithm
        checkIfIsSupportedCipherAlgorithm(
            algorithm = algorithm
        )
        val cipher = Cipher.getInstance(
            resolveTransformation(
                algorithm = algorithm,
                paddingType = paddingType,
                digestType = digestType
            )
        )
        return usage(cipher, key)
    }

    @Assembler
    private fun resolveTransformation(
        algorithm: String,
        paddingType: EncryptionPaddingType?,
        digestType: DigestType?,
    ): String {
        var transformation = "$algorithm/ECB"
        transformation += "/" + when (paddingType) {
            RSA_OAEP -> {
                if (digestType == null)
                    throw IllegalStateException("The OAEPPadding padding mode requires to specify the digest to use")
                digestType.oaepWithValue().value
            }

            RSA_PKCS1 -> paddingType.value
            else -> throw IllegalArgumentException("Invalid padding value")
        }
        return transformation
    }

    actual override fun deleteKey(
        alias: String,
    ) {
        serviceImpl.deleteKey(
            alias = alias
        )
    }

}