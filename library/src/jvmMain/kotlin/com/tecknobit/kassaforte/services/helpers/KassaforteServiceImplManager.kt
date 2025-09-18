package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.kassaforte.Kassaforte
import java.security.Key

internal class KassaforteServiceImplManager : KassaforteServiceManager<Key> {

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
    ): Key {
//        val kassaforte = Kassaforte(alias)
//        val encodedKeyData = kassaforte.unsuspendedWithdraw(
//            key = alias
//        )
//        if (encodedKeyData == null)
//            throw IllegalAccessException(KassaforteKeysService.IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR)
//        val decodedKeyData = Base64.decode(encodedKeyData)
//            .decodeToString()
//        val keyInfo: KeyInfo = Json.decodeFromString(decodedKeyData)
//        if (!keyInfo.canPerform(keyOperation))
//            throw RuntimeException(KassaforteKeysService.KEY_CANNOT_PERFORM_OPERATION_ERROR.format(keyOperation))
//        return keyInfo.resolveKey()
        TODO()
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