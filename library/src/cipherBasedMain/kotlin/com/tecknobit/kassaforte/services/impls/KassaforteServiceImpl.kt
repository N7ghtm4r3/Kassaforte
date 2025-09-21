package com.tecknobit.kassaforte.services.impls

import com.tecknobit.equinoxcore.annotations.Structure
import com.tecknobit.kassaforte.key.usages.KeyOperation
import java.security.Key

/**
 * The `KassaforteServiceImpl` class allows to implement a symmetric or aservice providing the basic behavior
 * that the service must have to correctly perform the operations for what is designed
 *
 * @author Tecknobit - N7ghtm4r3
 */
@Structure
internal abstract class KassaforteServiceImpl {

    /**
     * Method used to check whether the alias has been already taken to identify other key
     *
     * @param alias The alias to check
     *
     * @return whether the alias has been already taken as [Boolean]
     */
    abstract fun aliasExists(
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
    abstract fun getKey(
        alias: String,
        keyOperation: KeyOperation,
    ): Key

    /**
     * Method used to delete a generated key
     *
     * @param alias The alias of the key to delete
     */
    abstract fun deleteKey(
        alias: String,
    )

}