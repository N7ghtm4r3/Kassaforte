package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.RequiresDocumentation
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

    @RequiresDocumentation(
        additionalNotes = "INSERT SINCE Revision Two"
    )
    suspend fun verify(
        alias: String,
        digest: Digest,
        signature: String,
        message: Any,
    ): Boolean

    /**
     * Method used to delete a generated key
     *
     * @param alias The alias of the key to delete
     */
    override fun deleteKey(
        alias: String,
    )

}