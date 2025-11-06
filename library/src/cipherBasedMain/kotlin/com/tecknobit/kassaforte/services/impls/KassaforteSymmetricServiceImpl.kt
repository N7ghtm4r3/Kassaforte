package com.tecknobit.kassaforte.services.impls

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.BlockMode
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import java.security.Key

/**
 * The `KassaforteSymmetricServiceImpl` class allows to implement a service providing the basic behavior
 * that the service must have to correctly perform the operations with symmetric keys
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteServiceImpl
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect class KassaforteSymmetricServiceImpl() : KassaforteServiceImpl {

    /**
     * Method used to generate a new symmetric key
     *
     * @param alias The alias used to identify the key
     * @param algorithm The algorithm the key will use
     * @param keyGenSpec The generation spec to use to generate the key
     * @param purposes The purposes the key can be used
     */
    fun generateKey(
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
        alias: String,
    ): Boolean

    /**
     * Method used to get a key to perform a [keyOperation]
     *
     * @param alias The alias of the key to get
     * @param keyOperation The operation for what the key is being getting
     *
     * @return the specified key as [Key]
     */
    override fun getKey(
        alias: String,
        keyOperation: KeyOperation,
    ): Key

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
    fun resolveTransformation(
        algorithm: String,
        blockMode: BlockMode?,
        padding: EncryptionPadding?,
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