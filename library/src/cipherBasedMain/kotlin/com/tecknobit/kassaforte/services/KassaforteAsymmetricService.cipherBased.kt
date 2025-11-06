package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.Digest
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.RSA_OAEP
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.RSA_PKCS1
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyOperation.DECRYPT
import com.tecknobit.kassaforte.key.usages.KeyOperation.ENCRYPT
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.impls.KassaforteAsymmetricServiceImpl
import com.tecknobit.kassaforte.util.checkIfIsSupportedCipherAlgorithm
import com.tecknobit.kassaforte.util.checkIfIsSupportedType
import com.tecknobit.kassaforte.util.decode
import com.tecknobit.kassaforte.util.encode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.Key
import java.security.KeyPairGenerator
import javax.crypto.Cipher

/**
 * The `KassaforteAsymmetricService` object allows to generate and to use asymmetric keys and managing their persistence.
 *
 * It is based on the [Cipher] API to handling the encryption and decryption of the data and on the [KeyPairGenerator]
 * API to generate the pairs of keys
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteKeysService
 * @see AsymmetricKeyGenSpec
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteAsymmetricService : KassaforteKeysService<AsymmetricKeyGenSpec>() {

    /**
     * `serviceImpl` the platform specific implementation of the service
     */
    private val serviceImpl = KassaforteAsymmetricServiceImpl()

    /**
     * `serviceScope` the scope of the service to perform routines in background
     */
    private val serviceScope = CoroutineScope(
        context = Dispatchers.IO
    )

    /**
     * Method used to generate an asymmetric new key
     *
     * @param alias The alias used to identify the key
     * @param algorithm The algorithm the key will use
     * @param keyGenSpec The generation spec to use to generate the key
     * @param purposes The purposes the key can be used
     */
    actual override fun generateKey(
        alias: String,
        algorithm: Algorithm,
        keyGenSpec: AsymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        serviceScope.launch {
            serviceImpl.generateKey(
                algorithm = algorithm,
                alias = alias,
                keyGenSpec = keyGenSpec,
                purposes = purposes
            )
        }
    }

    /**
     * Method used to check whether the alias has been already taken to identify other key
     *
     * @param alias The alias to check
     *
     * @return whether the alias has been already taken as [Boolean]
     */
    actual override fun aliasExists(
        alias: String,
    ): Boolean {
        return serviceImpl.aliasExists(
            alias = alias
        )
    }

    /**
     * Method used to encrypt data with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param padding The padding to apply to encrypt data
     * @param digest The digest to apply to encrypt data
     * @param data The data to encrypt
     *
     * @return the encrypted data as [String]
     */
    actual suspend fun encrypt(
        alias: String,
        padding: EncryptionPadding?,
        digest: Digest?,
        data: Any,
    ): String {
        checkIfIsSupportedType(
            data = data
        )
        val cipherText = useCipher(
            alias = alias,
            keyOperation = ENCRYPT,
            padding = padding,
            digest = digest,
            usage = { cipher, key ->
                cipher.init(Cipher.ENCRYPT_MODE, key)
                val plainText = data.toString().encodeToByteArray()
                cipher.doFinal(plainText)
            }
        )
        return encode(cipherText)
    }

    /**
     * Method used to decrypt the encrypted data with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param padding The padding to apply to decrypt data
     * @param digest The digest to apply to decrypt data
     * @param data The data to decrypt
     *
     * @return the decrypted data as [String]
     */
    actual suspend fun decrypt(
        alias: String,
        padding: EncryptionPadding?,
        digest: Digest?,
        data: String,
    ): String {
        val plainText = useCipher(
            alias = alias,
            keyOperation = DECRYPT,
            padding = padding,
            digest = digest,
            usage = { cipher, key ->
                cipher.init(Cipher.DECRYPT_MODE, key)
                val cipherText = decode(data)
                cipher.doFinal(cipherText)
            }
        )
        return plainText.decodeToString()
    }

    /**
     * Method used to work and to use a [Cipher] instance to perform encryption or decryption of the data
     *
     * @param alias The alias which identify the key to use
     * @param keyOperation The operation the key have to perform
     * @param padding The padding to apply to ciphering data
     * @param digest The digest to apply to ciphering data
     * @param usage The routine the cipher have to perform
     *
     * @return the ciphered data as [ByteArray]
     */
    private inline fun useCipher(
        alias: String,
        keyOperation: KeyOperation,
        padding: EncryptionPadding?,
        digest: Digest?,
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
                padding = padding,
                digest = digest
            )
        )
        return usage(cipher, key)
    }

    /**
     * Method used to resolve the transformation value to obtain a cipher instance
     *
     * @param algorithm The algorithm to use
     * @param padding The padding to apply to ciphering data
     * @param digest The digest to apply to ciphering data
     *
     * @return the transformation value as [String]
     */
    @Assembler
    private fun resolveTransformation(
        algorithm: String,
        padding: EncryptionPadding?,
        digest: Digest?,
    ): String {
        var transformation = "$algorithm/ECB"
        transformation += "/" + when (padding) {
            RSA_OAEP -> {
                if (digest == null)
                    throw IllegalStateException("The OAEPPadding padding mode requires to specify the digest to use")
                digest.oaepWithValue().value
            }

            RSA_PKCS1 -> padding.value
            else -> throw IllegalArgumentException("Invalid padding value")
        }
        return transformation
    }

    /**
     * Method used to delete a generated key
     *
     * @param alias The alias of the key to delete
     */
    actual override fun deleteKey(
        alias: String,
    ) {
        serviceImpl.deleteKey(
            alias = alias
        )
    }

}