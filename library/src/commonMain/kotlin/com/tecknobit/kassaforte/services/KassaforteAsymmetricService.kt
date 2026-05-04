package com.tecknobit.kassaforte.services

import com.tecknobit.kassaforte.key.genspec.*
import com.tecknobit.kassaforte.key.usages.KeyPurposes

/**
 * The `KassaforteSymmetricService` object allows to generate and to use asymmetric keys and managing their persistence
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteKeysService
 * @see SymmetricKeyGenSpec
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object KassaforteAsymmetricService : KassaforteKeysService<AsymmetricKeyGenSpec> {

    /**
     * Method used to generate a new asymmetric key
     *
     * @param alias The alias used to identify the key
     * @param algorithm The algorithm the key will use
     * @param keyGenSpec The generation spec to use to generate the key
     * @param purposes The purposes the key can be used
     */
    override fun generateKey(
        alias: String,
        algorithm: Algorithm,
        keyGenSpec: AsymmetricKeyGenSpec,
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
        alias: String,
    ): Boolean

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
    suspend fun encrypt(
        alias: String,
        padding: EncryptionPadding? = null,
        digest: Digest? = null,
        data: Any,
    ): String

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
    suspend fun decrypt(
        alias: String,
        padding: EncryptionPadding? = null,
        digest: Digest? = null,
        data: String,
    ): String

    /**
     * Method used to sign messages with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param digest The digest to apply to sign messages
     * @param message The message to sign
     *
     * @return the signed message as [String]
     *
     * @since Revision Two
     */
    suspend fun sign(
        alias: String,
        digest: Digest,
        message: Any,
    ): String

    /**
     * Method used to verify the validity of the messages previously signed with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param digest The digest applied to sign [message]
     * @param message The message to verify
     * @param signature The signature previously computed
     *
     * @return whether the message matches to [signature] as [Boolean]
     *
     * @since Revision Two
     */
    suspend fun verify(
        alias: String,
        digest: Digest,
        message: Any,
        signature: String,
    ): Boolean

    /**
     * Method to perform an `Envelope Encryption` for wrapping a `DEK` material
     *
     * @param kekAlias The alias which identify the `KEK` key to use
     * @param padding The padding to apply to wrap the material
     * @param digest The digest to apply to wrap material
     * @param dekBytes Arbitrary bytes representing the `DEK` material to wrap
     *
     * @return the [dekBytes] wrapped using the specified KEK key as `Base64` [String]
     *
     * @since Revision Three
     */
    suspend fun wrap(
        kekAlias: String,
        padding: EncryptionPadding,
        digest: Digest,
        dekBytes: ByteArray,
    ): String

    /**
     * Method to perform an `Envelope Decryption` for unwrapping a `DEK` material previously
     * wrapped
     *
     * @param kekAlias The alias which identify the `KEK` key to use
     * @param padding The padding to apply to unwrap the material
     * @param digest The digest to apply to unwrap the material
     * @param wrappedDek The wrapped material, `Base64` encoded, to unwrap
     *
     * @return the material unwrapped using the specified KEK key as [ByteArray]
     *
     * @since Revision Three
     */
    suspend fun unwrap(
        kekAlias: String,
        padding: EncryptionPadding,
        digest: Digest,
        wrappedDek: String,
    ): ByteArray

    /**
     * Method to perform a key agreement and obtain a shared secret
     *
     * @param alias The alias of the private key used in the agreement
     * @param peerPublicKey The remote peer public key used to compute the shared secret
     * @param publicKeyLength The length of the public key
     * @param secretLength The length the shared secret must have
     *
     * @return the shared secret generated with the agreement as `Base64` encoded [String]
     *
     * @since Revision Three
     */
    suspend fun agree(
        alias: String,
        peerPublicKey: ByteArray,
        publicKeyLength: KeySize,
        secretLength: KeySize = publicKeyLength,
    ): String

    /**
     * Method used to delete a generated key
     *
     * @param alias The alias of the key to delete
     */
    override fun deleteKey(
        alias: String,
    )

}