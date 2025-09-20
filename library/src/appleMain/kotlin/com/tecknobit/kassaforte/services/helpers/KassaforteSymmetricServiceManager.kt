package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.kassaforte.Kassaforte
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR

internal class KassaforteSymmetricServiceManager : KassaforteServiceImplManager() {

    override fun isAliasTaken(
        alias: String,
    ): Boolean {
        val kassaforte = Kassaforte(alias)
        return kassaforte.unsuspendedWithdraw(
            key = alias
        ) != null
    }

    override fun retrieveKey(
        alias: String,
    ): String {
        val kassaforte = Kassaforte(alias)
        return kassaforte.unsuspendedWithdraw(
            key = alias
        ) ?: throw RuntimeException(IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR)
    }

    override fun removeKey(
        alias: String,
    ) {
        val kassaforte = Kassaforte(alias)
        kassaforte.remove(
            key = alias
        )
    }

}