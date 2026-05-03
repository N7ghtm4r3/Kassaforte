package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Implementation
import com.tecknobit.kassaforte.key.KassaforteDerivedKey
import com.tecknobit.kassaforte.key.genspec.*
import com.tecknobit.kassaforte.key.genspec.BlockMode.GCM
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.NONE
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyOperation.*
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.impls.KassaforteSymmetricServiceImpl
import com.tecknobit.kassaforte.util.decode
import com.tecknobit.kassaforte.util.encode
import com.tecknobit.kassaforte.util.encodeForKeyOperation
import java.security.Key
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec

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
        return encryptImpl(
            alias = alias,
            keyOperation = ENCRYPT,
            blockMode = blockMode,
            padding = padding,
            data = data
        )
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
        return decryptImpl(
            alias = alias,
            keyOperation = DECRYPT,
            blockMode = blockMode,
            padding = padding,
            data = data
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

    /**
     * Method to perform an `Envelopment Encryption` for wrapping a `DEK` material
     *
     * @param kekAlias The alias which identify the `KEK` key to use
     * @param dekBytes Arbitrary bytes representing the `DEK` material to wrap
     *
     * @return the [dekBytes] wrapped using the specified KEK key as `Base64` [String]
     *
     * @since Revision Three
     */
    actual suspend fun wrap(
        kekAlias: String,
        dekBytes: ByteArray,
    ): String {
        return encryptImpl(
            alias = kekAlias,
            keyOperation = WRAP,
            blockMode = GCM,
            padding = NONE,
            data = encode(dekBytes)
        )
    }

    /**
     * Method to perform an `Envelopment Decryption` for unwrapping a `DEK` material previously
     * wrapped
     *
     * @param kekAlias The alias which identify the `KEK` key to use
     * @param wrappedDek The wrapped material, `Base64` encoded, to unwrap
     *
     * @return the material unwrapped using the specified KEK key as [ByteArray]
     *
     * @since Revision Three
     */
    actual suspend fun unwrap(
        kekAlias: String,
        wrappedDek: String,
    ): ByteArray {
        val unwrappedDek = decryptImpl(
            alias = kekAlias,
            keyOperation = UNWRAP,
            blockMode = GCM,
            padding = NONE,
            data = wrappedDek
        )

        return decode(unwrappedDek)
    }

    /**
     * Implementation method used to encrypt data with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param keyOperation [KeyOperation.ENCRYPT] or [KeyOperation.WRAP]
     * @param blockMode The block mode to use to encrypt data
     * @param padding The padding to apply to encrypt data
     * @param data The data to encrypt
     *
     * @return the encrypted data as [String]
     *
     * @since Revision Three
     */
    @Implementation
    private fun encryptImpl(
        alias: String,
        keyOperation: KeyOperation,
        blockMode: BlockMode,
        padding: EncryptionPadding,
        data: Any,
    ): String {
        var cipherIv: ByteArray = byteArrayOf()
        var encryptedData = useCipher(
            alias = alias,
            blockMode = blockMode,
            padding = padding,
            keyOperation = keyOperation
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
     * Implementation method used to decrypt encrypted data with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param keyOperation [KeyOperation.DECRYPT] or [KeyOperation.UNWRAP]
     * @param blockMode The block mode to use to decrypt data
     * @param padding The padding to apply to decrypt data
     * @param data The data to decrypt
     *
     * @return the decrypted data as [String]
     */
    @Implementation
    private fun decryptImpl(
        alias: String,
        keyOperation: KeyOperation,
        blockMode: BlockMode,
        padding: EncryptionPadding,
        data: String,
    ): String {
        val decryptedData = useCipher(
            alias = alias,
            blockMode = blockMode,
            padding = padding,
            keyOperation = keyOperation
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

    // TODO: TO DOCU SINCE
    actual suspend fun deriveKey(
        password: CharArray,
        salt: ByteArray,
        iterationCount: Int,
        keySize: KeySize,
        digest: Digest,
    ): KassaforteDerivedKey {
        val spec = PBEKeySpec(
            password,
            salt,
            iterationCount,
            keySize.bitCount
        )

        val factory = SecretKeyFactory.getInstance(Algorithm.PBKDF2.value + digest)
        val secretKey = factory.generateSecret(spec)

        return KassaforteDerivedKey(
            key = encode(secretKey.encoded),
            salt = salt,
            iterationCount = iterationCount,
            keySize = keySize,
            digest = digest
        )
    }

}