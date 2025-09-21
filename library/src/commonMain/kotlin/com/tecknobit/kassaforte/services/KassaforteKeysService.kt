package com.tecknobit.kassaforte.services

import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.KassaforteKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyPurposes

/**
 * The `KassaforteKeysService` class allows to generate keys and managing their persistence
 *
 * @param KS The type of the generation spec used to generate the key
 *
 * @author Tecknobit - N7ghtm4r3
 */
// TODO: TO ANNOTATE WITH @Structure
abstract class KassaforteKeysService<KS: KassaforteKeyGenSpec> {

    companion object {

        /**
         * `ALIAS_ALREADY_TAKEN_ERROR` the error message to display when the specified alias is not available
         */
        const val ALIAS_ALREADY_TAKEN_ERROR = "This alias is already taken"

        /**
         * `IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR` the error message to display when an error occurred during the retrieval of
         * the key
         */
        const val IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR = "Impossible to retrieve the specified key"

        /**
         * `KEY_CANNOT_PERFORM_OPERATION_ERROR` the error message to display when the key cannot perform the specified
         * operation
         */
        const val KEY_CANNOT_PERFORM_OPERATION_ERROR = "The key cannot perform this operation: %s"

        /**
         * `INVALID_ASYMETRIC_ALGORITHM` the error message to display when the specified algorithm is not supposed to be
         * asymmetric
         */
        const val INVALID_ASYMETRIC_ALGORITHM = "Invalid asymmetric algorithm"

        /**
         * `INVALID_ENCRYPTION_PADDING` the error message to display when the specified encryption padding is not valid
         */
        const val INVALID_ENCRYPTION_PADDING = "Invalid encryption padding"

    }

    /**
     * Method used to generate a new key
     *
     * @param algorithm The algorithm the key will use
     * @param alias The alias used to identify the key
     * @param keyGenSpec The generation spec to use to generate the key
     * @param purposes The purposes the key can be used
     */
    abstract fun generateKey(
        algorithm: Algorithm,
        alias: String,
        keyGenSpec: KS,
        purposes: KeyPurposes,
    )

    /**
     * Method used to check whether the alias has been already taken to identify other key
     *
     * @param alias The alias to check
     *
     * @return whether the alias has been already taken as [Boolean]
     */
    protected abstract fun aliasExists(
        alias: String
    ): Boolean

    /**
     * Method used to delete a generated key
     *
     * @param alias The alias of the key to delete
     */
    abstract fun deleteKey(
        alias: String
    )

}