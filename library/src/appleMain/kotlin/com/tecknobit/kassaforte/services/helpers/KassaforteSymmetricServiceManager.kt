package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.kassaforte.Kassaforte
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR

/**
 * The `KassaforteSymmetricServiceManager` class allows to perform keychain's operations on symmetric keys
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteServiceManager
 * @see KassaforteServiceImplManager
 */
internal class KassaforteSymmetricServiceManager : KassaforteServiceImplManager<String>() {

    /**
     * Method used to check whether the alias has been already taken to identify other key
     *
     * @param alias The alias to check
     *
     * @return whether the alias has been already taken as [Boolean]
     */
    override fun isAliasTaken(
        alias: String,
    ): Boolean {
        val kassaforte = Kassaforte(alias)
        return kassaforte.unsuspendedWithdraw(
            key = alias
        ) != null
    }

    /**
     * Method used to retrieve from the secure storage the specified key
     *
     * @param alias The alias used to store the key
     *
     * @return the key as [String]
     */
    override fun retrieveKey(
        alias: String,
    ): String {
        val kassaforte = Kassaforte(alias)
        return kassaforte.unsuspendedWithdraw(
            key = alias
        ) ?: throw RuntimeException(IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR)
    }

    /**
     * Method used to remove from the secure store the specified key
     *
     * @param alias The alias of the key to remove
     */
    override fun removeKey(
        alias: String,
    ) {
        val kassaforte = Kassaforte(alias)
        kassaforte.remove(
            key = alias
        )
    }

}