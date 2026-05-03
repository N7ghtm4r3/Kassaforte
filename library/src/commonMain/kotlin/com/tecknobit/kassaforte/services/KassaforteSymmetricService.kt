package com.tecknobit.kassaforte.services

import com.tecknobit.kassaforte.key.KassaforteDerivedKey
import com.tecknobit.kassaforte.key.genspec.*
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.NONE
import com.tecknobit.kassaforte.key.usages.KeyPurposes

/**
 * The `KassaforteSymmetricService` object allows to generate and to use symmetric keys and managing their persistence
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteKeysService
 * @see SymmetricKeyGenSpec
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object KassaforteSymmetricService: KassaforteKeysService<SymmetricKeyGenSpec> {

    /**
     * Method used to generate a new symmetric key
     *
     * @param alias The alias used to identify the key
     * @param algorithm The algorithm the key will use, at the moment will be overridden by default with
     * [Algorithm.AES] value
     * @param keyGenSpec The generation spec to use to generate the key
     * @param purposes The purposes the key can be used
     */
    override fun generateKey(
        alias: String,
        algorithm: Algorithm,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    )

    /**
     * Method used to check whether the alias has been already taken to identify other key
     *
     * @param alias The alias to check
     *
     * @return whether the alias has been already taken as [Boolean]
     */
    override fun aliasExists(
        alias: String
    ): Boolean

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
    suspend fun encrypt(
        alias: String,
        blockMode: BlockMode,
        padding: EncryptionPadding = NONE,
        data: Any,
    ): String

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
    suspend fun decrypt(
        alias: String,
        blockMode: BlockMode,
        padding: EncryptionPadding = NONE,
        data: String,
    ): String

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
    suspend fun sign(
        alias: String,
        message: Any,
    ): String

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
    suspend fun verify(
        alias: String,
        message: Any,
        signature: String,
    ): Boolean

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
    suspend fun wrap(
        kekAlias: String,
        dekBytes: ByteArray,
    ): String

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
    suspend fun unwrap(
        kekAlias: String,
        wrappedDek: String,
    ): ByteArray

    // TODO: TO DOCU SINCE
    suspend fun deriveKey(
        password: CharArray,
        salt: ByteArray,
        iterationCount: Int = 600_000,
        keySize: KeySize,
        digest: Digest = Digest.SHA256,
    ): KassaforteDerivedKey

    /**
     * Method used to delete a generated key
     *
     * @param alias The alias of the key to delete
     */
    override fun deleteKey(
        alias: String
    )

}


