@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.RequiresDocumentation
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.Kassaforte
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.Algorithm.*
import com.tecknobit.kassaforte.key.genspec.BlockMode
import com.tecknobit.kassaforte.key.genspec.BlockMode.CTR
import com.tecknobit.kassaforte.key.genspec.BlockMode.GCM
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyDetailsSheet
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyOperation.*
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.helpers.KassaforteSymmetricServiceManager
import com.tecknobit.kassaforte.util.decode
import com.tecknobit.kassaforte.util.encode
import com.tecknobit.kassaforte.util.encodeForKeyOperation
import korlibs.crypto.*
import korlibs.crypto.AES
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import platform.CoreCrypto.*
import platform.Security.SecRandomCopyBytes
import platform.Security.kSecRandomDefault

/**
 * The `KassaforteSymmetricService` class allows to generate and to use symmetric keys and managing their persistence.
 *
 * It is based on the [SecRandomCopyBytes](https://developer.apple.com/documentation/security/secrandomcopybytes(_:_:_:))
 * method for the generation of the symmetric keys, and for their secure storage uses the
 * [Keychain](https://developer.apple.com/documentation/security/keychain-services) APIs
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteKeysService
 * @see SymmetricKeyGenSpec
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteSymmetricService : KassaforteKeysService<SymmetricKeyGenSpec>() {

    /**
     * `serviceManager` instance of the manager which helps the service to perform the operations with the keys
     */
    private val serviceManager = KassaforteSymmetricServiceManager()

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
                algorithm = algorithm,
                keyPurposes = purposes,
                key = keyBytes
            )
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
        return serviceManager.isAliasTaken(
            alias = alias
        )
    }

    /**
     * Method used to store the data of the generated key
     *
     * @param alias The alias which identify the key
     * @param keyInfo The extra information of the generated key to store
     */
    private fun storeKeyData(
        alias: String,
        keyInfo: KeyInfo,
    ) {
        val encodedKeyData = formatKeyData(
            keyInfo = keyInfo
        )
        val kassaforte = Kassaforte(alias)
        kassaforte.safeguard(
            key = alias,
            data = encodedKeyData
        )
    }

    /**
     * Method used to format the data of the key
     *
     * @param keyInfo The extra information of the generated key
     *
     * @return the data of the key formatted as [String]
     */
    @Returner
    private inline fun formatKeyData(
        keyInfo: KeyInfo,
    ): String {
        val encodedKeyInfo = Json.encodeToString(keyInfo)
        return encode(encodedKeyInfo)
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
        val iv = ByteArray(blockMode.blockSize).apply {
            SecureRandom.nextBytes(this)
        }
        val encryptedData = useCipher(
            alias = alias,
            keyOperation = ENCRYPT,
            blockMode = blockMode,
            iv = iv,
            usage = { cipher ->
                val dataToEncrypt = data.encodeForKeyOperation()
                cipher.encrypt(dataToEncrypt)
            }
        )
        return encode(iv + encryptedData)
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
        val dataToDecrypt = decode(data)
        val blockSize = blockMode.blockSize
        val iv = dataToDecrypt.copyOfRange(0, blockSize)
        val cipherText = dataToDecrypt.copyOfRange(blockSize, dataToDecrypt.size)
        return useCipher(
            alias = alias,
            keyOperation = DECRYPT,
            blockMode = blockMode,
            iv = iv,
            usage = { cipher -> cipher.decrypt(cipherText) }
        ).decodeToString()
    }

    /**
     * Method used to work and to use a key (private or public) to perform encryption or decryption of the data
     *
     * @param alias The alias which identify the key to use
     * @param keyOperation The operation the key have to perform
     * @param blockMode The block mode to use to ciphering data
     * @param iv The initialization vector to use to perform the ciphering
     * @param usage The routine the cipher have to perform
     *
     * @return the ciphered data as [ByteArray]
     */
    private inline fun useCipher(
        alias: String,
        keyOperation: KeyOperation,
        blockMode: BlockMode,
        iv: ByteArray,
        usage: (CipherWithModeAndPadding) -> ByteArray,
    ): ByteArray {
        // TODO: to remove when GCM integrated
        if (blockMode == GCM)
            throw RuntimeException("GCM on iOS is currently missing, use CBC or CTR instead")
        val keyInfo = getKeyInfo(
            alias = alias,
            keyOperation = keyOperation
        )
        // TODO: WHEN GCM AVAILABLE INTEGRATE IT
        val cipher = AES(keyInfo.key).get(
            mode = when (blockMode) {
                CTR -> CipherMode.CTR
                else -> CipherMode.CBC
            },
            padding = when (blockMode) {
                CTR -> CipherPadding.NoPadding
                else -> CipherPadding.PKCS7Padding
            },
            iv = iv
        )
        return usage(cipher)
    }

    /**
     * Method used to sign data with the key specified by the [alias] value
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
        val keyInfo = getKeyInfo(
            alias = alias,
            keyOperation = SIGN
        )
        val key = keyInfo.key
        val algorithm = keyInfo.algorithm
        val signedMessage = algorithm.resolveHMACOutRef()
        signedMessage.usePinned { messageRef ->
            val messageData = message.encodeForKeyOperation()
            key.usePinned { pinnedKey ->
                messageData.usePinned { pinnedMessage ->
                    CCHmac(
                        algorithm = keyInfo.algorithm
                            .resolveHMACAlgorithm(),
                        key = pinnedKey.addressOf(0),
                        keyLength = key.size.toULong(),
                        data = pinnedMessage.addressOf(0),
                        dataLength = messageData.size.toULong(),
                        macOut = messageRef.addressOf(0)
                    )
                }
            }
        }
        return encode(signedMessage)
    }

    @RequiresDocumentation(
        additionalNotes = "TO INSERT SINCE Revision Two"
    )
    actual suspend fun verify(
        alias: String,
        message: Any,
        hmac: String,
    ): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Method used to resolve the out reference where the output of the [sign] method could be written
     *
     * @return the instance where the result of the `HMAC` can be written as [ByteArray]
     *
     * @since Revision Two
     */
    @Returner
    private fun Algorithm.resolveHMACOutRef(): ByteArray {
        return ByteArray(
            when (this) {
                HMAC_SHA1 -> 20
                HMAC_SHA256 -> 32
                HMAC_SHA384 -> 48
                HMAC_SHA512 -> 64
                else -> throw IllegalArgumentException("Invalid algorithm to perform the sign")
            }
        )
    }

    /**
     * Method used to resolve which `SHA` function is requested to perform the singing
     *
     * @return the hash function algorithm as [CCHmacAlgorithm]
     *
     * @since Revision Two
     */
    @Returner
    private fun Algorithm.resolveHMACAlgorithm(): CCHmacAlgorithm {
        return when (this) {
            HMAC_SHA1 -> kCCHmacAlgSHA1
            HMAC_SHA256 -> kCCHmacAlgSHA256
            HMAC_SHA384 -> kCCHmacAlgSHA384
            HMAC_SHA512 -> kCCHmacAlgSHA512
            else -> throw IllegalArgumentException("Invalid algorithm to perform the sign")
        }
    }

    /**
     * Method used to get the key and the related information from the secure storage
     *
     * @param alias The alias which identify the key to use
     * @param keyOperation The operation the key have to perform
     *
     * @return the key and related information as [KeyInfo]
     *
     * @since Revision Two
     */
    private fun getKeyInfo(
        alias: String,
        keyOperation: KeyOperation,
    ): KeyInfo {
        val kassaforte = Kassaforte(alias)
        val encodedKeyData = kassaforte.unsuspendedWithdraw(
            key = alias
        )
        if (encodedKeyData == null)
            throw RuntimeException(IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR)
        val decodedKeyData = decode(encodedKeyData).decodeToString()
        val keyInfo: KeyInfo = Json.decodeFromString(decodedKeyData)
        if (!keyInfo.canPerform(keyOperation))
            throw RuntimeException(KEY_CANNOT_PERFORM_OPERATION_ERROR.replace("%s", keyOperation.name))
        return keyInfo
    }

    /**
     * Method used to delete a generated key
     *
     * @param alias The alias of the key to delete
     */
    actual override fun deleteKey(
        alias: String
    ) {
        serviceManager.removeKey(
            alias = alias
        )
    }

    /**
     * The `KeyInfo` data class allows to store extra information related to a generated key such as the usages the key
     * can be used
     *
     * @property keyPurposes The purposes the key can be used
     * @property key The generated symmetric key
     * @property algorithm The algorithm the key will use
     *
     * @author Tecknobit - N7ghtm4r3
     *
     * @see KeyDetailsSheet
     */
    @Serializable
    private data class KeyInfo(
        val algorithm: Algorithm,
        override val keyPurposes: KeyPurposes,
        override val key: ByteArray,
    ) : KeyDetailsSheet<ByteArray> {

        /**
         * Indicates whether some other object is "equal to" this one.
         *
         * Implementations must fulfil the following requirements:
         * * Reflexive: for any non-null value `x`, `x.equals(x)` should return true.
         * * Symmetric: for any non-null values `x` and `y`, `x.equals(y)` should return true if and only if `y.equals(x)` returns true.
         * * Transitive: for any non-null values `x`, `y`, and `z`, if `x.equals(y)` returns true and `y.equals(z)` returns true, then `x.equals(z)` should return true.
         * * Consistent: for any non-null values `x` and `y`, multiple invocations of `x.equals(y)` consistently return true or consistently return false, provided no information used in `equals` comparisons on the objects is modified.
         * * Never equal to null: for any non-null value `x`, `x.equals(null)` should return false.
         *
         * Read more about [equality](https://kotlinlang.org/docs/reference/equality.html) in Kotlin.
         */
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is KeyInfo) return false

            if (algorithm != other.algorithm) return false
            if (keyPurposes != other.keyPurposes) return false
            if (!key.contentEquals(other.key)) return false

            return true
        }

        /**
         * Returns a hash code value for the object.
         *
         * The general contract of `hashCode` is:
         * * Whenever it is invoked on the same object more than once, the `hashCode` method must consistently return the same integer, provided no information used in `equals` comparisons on the object is modified.
         * * If two objects are equal according to the `equals()` method, then calling the `hashCode` method on each of the two objects must produce the same integer result.
         */
        override fun hashCode(): Int {
            var result = algorithm.hashCode()
            result = 31 * result + keyPurposes.hashCode()
            result = 31 * result + key.contentHashCode()
            return result
        }

    }

}