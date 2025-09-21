package com.tecknobit.kassaforte.services

import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.BlockMode
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.NONE
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
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
     * @param algorithm The algorithm the key will use, at the moment will be overridden by default with
     * [Algorithm.AES] value
     * @param alias The alias used to identify the key
     * @param keyGenSpec The generation spec to use to generate the key
     * @param purposes The purposes the key can be used
     */
    override fun generateKey(
        algorithm: Algorithm,
        alias: String,
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
     * Method used to delete a generated key
     *
     * @param alias The alias of the key to delete
     */
    override fun deleteKey(
        alias: String
    )

}


