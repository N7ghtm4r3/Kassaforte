package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.equinoxcore.annotations.Structure

/**
 * The `KassaforteServiceManager` interface allows to provide the basic operations a service manager can perform.
 *
 * The managers are particularly useful when the implementations of the symmetric and asymmetric services sharing lot of
 * common code that can be delegated to an external manager avoiding breaking the `expect/actual` implementation
 *
 * @param K The type of the key
 *
 * @author Tecknobit - N7ghtm4r3
 */
@Structure
interface KassaforteServiceManager<K> {

    /**
     * Method used to check whether the alias has been already taken to identify other key
     *
     * @param alias The alias to check
     *
     * @return whether the alias has been already taken as [Boolean]
     */
    fun isAliasTaken(
        alias: String,
    ): Boolean

    /**
     * Method used to retrieve from the secure storage the specified key
     *
     * @param alias The alias used to store the key
     *
     * @return the key as [K]
     */
    fun retrieveKey(
        alias: String,
    ): K

    /**
     * Method used to remove from the secure store the specified key
     *
     * @param alias The alias of the key to remove
     */
    fun removeKey(
        alias: String,
    )

}