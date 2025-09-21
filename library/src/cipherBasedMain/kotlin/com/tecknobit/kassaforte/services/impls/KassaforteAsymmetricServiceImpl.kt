package com.tecknobit.kassaforte.services.impls

import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import java.security.Key

/**
 * The `KassaforteAsymmetricServiceImpl` class allows to implement an asymmetric service providing the basic behavior
 * that the service must have to correctly perform the operations with asymmetric keys
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteServiceImpl
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect class KassaforteAsymmetricServiceImpl() : KassaforteServiceImpl {

    /**
     * Method used to generate an asymmetric new key
     *
     * @param algorithm The algorithm the key will use
     * @param alias The alias used to identify the key
     * @param keyGenSpec The generation spec to use to generate the key
     * @param purposes The purposes the key can be used
     */
    fun generateKey(
        algorithm: Algorithm,
        alias: String,
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
     * Method used to delete a generated key
     *
     * @param alias The alias of the key to delete
     */
    override fun deleteKey(
        alias: String,
    )

}
