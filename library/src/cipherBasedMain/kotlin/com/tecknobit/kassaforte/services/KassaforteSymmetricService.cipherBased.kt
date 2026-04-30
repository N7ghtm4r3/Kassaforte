package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.BlockMode
import com.tecknobit.kassaforte.key.genspec.BlockMode.GCM
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.NONE
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyOperation.*
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.impls.KassaforteSymmetricServiceImpl
import com.tecknobit.kassaforte.util.decode
import com.tecknobit.kassaforte.util.encode
import com.tecknobit.kassaforte.util.encodeForKeyOperation
import java.security.Key
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.Cipher.UNWRAP_MODE
import javax.crypto.Cipher.WRAP_MODE
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * The `KassaforteKeysService` object allows to generate and to use symmetric keys and managing their persistence.
 *
 * It is based on the [Cipher] API to handling the encryption and decryption of the data and on the [KeyGenerator] API
 * to generate the keys
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteKeysService
 * @see SymmetricKeyGenSpec
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteSymmetricService : KassaforteKeysService<SymmetricKeyGenSpec>() {

    /**
     * `serviceImpl` the platform specific implementation of the service
     */
    private val serviceImpl = KassaforteSymmetricServiceImpl()

    /**
     * Method used to generate a new symmetric key
     *
     * @param alias The alias used to identify the key
     * @param algorithm The algorithm the key will use
     * @param keyGenSpec The generation spec to use to generate the key
     * @param purposes The purposes the key can be used
     */
    actual override fun generateKey(
        alias: String,
        algorithm: Algorithm,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        serviceImpl.generateKey(
            alias = alias,
            algorithm = algorithm,
            keyGenSpec = keyGenSpec,
            purposes = purposes
        )
    }

    /**
     * Method used to check whether the alias has been already taken to identify other key
     *
     * @param alias The alias to check
     *
     * @return whether the alias has been already taken as [Boolean]
     */
    actual override fun aliasExists(
        alias: String
    ): Boolean {
        return serviceImpl.aliasExists(
            alias = alias
        )
    }

    /**
     * Method used to encrypt data with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param blockMode The block mode to use to encrypt data
     * @param padding The padding to apply to encrypt data
     * @param data The data to encrypt
     *
     * @return the encrypted data as [String]
     */
    actual suspend fun encrypt(
        alias: String,
        blockMode: BlockMode,
        padding: EncryptionPadding,
        data: Any,
    ): String {
        var cipherIv: ByteArray = byteArrayOf()
        var encryptedData = useCipher(
            alias = alias,
            blockMode = blockMode,
            padding = padding,
            keyOperation = ENCRYPT
        ) { cipher, key ->
            cipher.init(Cipher.ENCRYPT_MODE, key)

            cipherIv = cipher.iv
            val dataToEncrypt = data.encodeForKeyOperation()
            cipher.doFinal(dataToEncrypt)
        }
        encryptedData = cipherIv + encryptedData

        return encode(encryptedData)
    }

    /**
     * Method used to decrypt encrypted data with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param blockMode The block mode to use to decrypt data
     * @param padding The padding to apply to decrypt data
     * @param data The data to decrypt
     *
     * @return the decrypted data as [String]
     */
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
            val dataToDecrypt = decode(data)
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

    /**
     * Method used to work and to use a [Cipher] instance to perform encryption or decryption of the data
     *
     * @param alias The alias which identify the key to use
     * @param keyOperation The operation the key have to perform
     * @param blockMode The block mode to use to ciphering data
     * @param padding The padding to apply to ciphering data
     * @param usage The routine the cipher have to perform
     *
     * @return the ciphered data as [ByteArray]
     */
    private inline fun useCipher(
        alias: String,
        keyOperation: KeyOperation,
        blockMode: BlockMode? = null,
        padding: EncryptionPadding? = null,
        crossinline usage: (Cipher, Key) -> ByteArray,
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

        return usage(cipher, key)
    }

    /**
     * Method used to resolve the transformation value to obtain a cipher instance
     *
     * @param algorithm The algorithm to use
     * @param blockMode The block mode to use to ciphering data
     * @param padding The padding to apply to ciphering data
     *
     * @return the transformation value as [String]
     */
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

    /**
     * Method used to sign messages with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param message The message to sign
     *
     * @return the signed message as [String]
     *
     * @since Revision Two
     */
    actual suspend fun sign(
        alias: String,
        message: Any,
    ): String {
        val signedMessage = useMac(
            alias = alias,
            usage = { mac -> mac.doFinal(message.encodeForKeyOperation()) }
        )

        return encode(signedMessage)
    }

    /**
     * Method used to verify the validity of the messages previously signed with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param message The message to verify
     * @param signature The signature previously computed
     *
     * @return whether the message matches to [signature] as [Boolean]
     *
     * @since Revision Two
     */
    actual suspend fun verify(
        alias: String,
        message: Any,
        signature: String,
    ): Boolean {
        val verification = sign(
            alias = alias,
            message = message
        )
        val digestA = decode(signature)
        val digestB = decode(verification)

        return MessageDigest.isEqual(digestA, digestB)
    }

    /**
     * Method used to work and to use a [Mac] instance to perform signing or verifying of the data
     *
     * @param alias The alias which identify the key to use
     * @param usage The routine the mac instance have to perform
     *
     * @return the message processed by the [Mac] instance as [ByteArray]
     *
     * @since Revision Two
     */
    private inline fun useMac(
        alias: String,
        crossinline usage: (Mac) -> ByteArray,
    ): ByteArray {
        val key = serviceImpl.getKey(
            alias = alias,
            keyOperation = SIGN
        )

        val mac = Mac.getInstance(key.algorithm)
        mac.init(key)
        return usage(mac)
    }

    // TODO: TO DOCU SINCE AND ADD DESCRIBE THE FALLBACK MECHANISM
    actual suspend fun wrap(
        kekAlias: String,
        kekAlgorithm: Algorithm,
        dekBytes: ByteArray,
    ): String {
        val dek = SecretKeySpec(dekBytes, Algorithm.AES.value)

        val wrappedDek = try {
            val wrappedSource = useCipher(
                alias = kekAlias,
                algorithm = kekAlgorithm,
                usage = { cipher, kek ->
                    cipher.init(WRAP_MODE, kek)

                    cipher.wrap(dek)
                },
            )

            encode(wrappedSource)
        } catch (_: NoSuchAlgorithmException) {
            fallbackWrap(
                kekAlias = kekAlias,
                dek = dek
            )
        }

        return wrappedDek
    }

    // TODO: TO DOCU
    private suspend fun fallbackWrap(
        kekAlias: String,
        dek: SecretKey,
    ): String {
        val wrappedDek = encrypt(
            alias = kekAlias,
            blockMode = GCM,
            padding = NONE,
            data = encode(dek.encoded)
        )

        return wrappedDek
    }

    // TODO: TO DOCU SINCE AND ADD DESCRIBE THE FALLBACK MECHANISM
    actual suspend fun unwrap(
        kekAlias: String,
        kekAlgorithm: Algorithm,
        wrappedDek: String,
        dekAlgorithm: Algorithm,
    ): ByteArray {
        val wrappedDekSourceBytes = decode(wrappedDek)

        val unwrappedDek = try {
            useCipher(
                alias = kekAlias,
                algorithm = kekAlgorithm,
                usage = { cipher, kek ->
                    cipher.init(UNWRAP_MODE, kek)
                    val key = cipher.unwrap(wrappedDekSourceBytes, dekAlgorithm.value, Cipher.SECRET_KEY)

                    key.encoded
                },
            )
        } catch (_: NoSuchAlgorithmException) {
            fallbackUnwrap(
                kekAlias = kekAlias,
                wrappedDek = wrappedDek
            )
        }

        return unwrappedDek
    }

    // TODO: TO DOCU
    private suspend fun fallbackUnwrap(
        kekAlias: String,
        wrappedDek: String,
    ): ByteArray {
        val unwrappedDek = decrypt(
            alias = kekAlias,
            blockMode = GCM,
            padding = NONE,
            data = wrappedDek
        )

        return decode(unwrappedDek)
    }

    // TODO: TO DOCU
    private inline fun useCipher(
        alias: String,
        algorithm: Algorithm,
        crossinline usage: (Cipher, Key) -> ByteArray,
    ): ByteArray {
        val key = serviceImpl.getKey(
            alias = alias,
            keyOperation = WRAP
        )
        val transformation = "${algorithm.value}/${NONE.value}"
        val cipher = Cipher.getInstance(transformation)

        return usage(cipher, key)
    }

    /**
     * Method used to delete a generated key
     *
     * @param alias The alias of the key to delete
     */
    actual override fun deleteKey(
        alias: String
    ) {
        serviceImpl.deleteKey(
            alias = alias
        )
    }

}