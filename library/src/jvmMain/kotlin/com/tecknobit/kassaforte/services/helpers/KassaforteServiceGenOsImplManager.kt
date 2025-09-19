package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.kassaforte.Kassaforte
import com.tecknobit.kassaforte.key.usages.KeyDetailsSheet
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64

internal class KassaforteServiceGenOsImplManager<KI : KeyDetailsSheet<*>>(
    serializer: KSerializer<KI>,
) : KassaforteServiceImplManager<KI>(
    serializer = serializer
) {

    override fun isAliasTaken(
        alias: String,
    ): Boolean {
        val kassaforte = Kassaforte(alias)
        return kassaforte.unsuspendedWithdraw(
            key = alias
        ) != null
    }

    override fun storeKeyData(
        alias: String,
        keyInfo: KI,
    ) {
        val keyData = formatKeyData(
            keyInfo = keyInfo
        )
        val kassaforte = Kassaforte(alias)
        kassaforte.safeguard(
            key = alias,
            data = keyData
        )
    }

    override fun retrieveKey(
        alias: String,
    ): KI {
        val kassaforte = Kassaforte(alias)
        val encodedKeyData = kassaforte.unsuspendedWithdraw(
            key = alias
        )
        if (encodedKeyData == null)
            throw IllegalAccessException(IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR)
        val storedData = Base64.decode(encodedKeyData).decodeToString()
        return Json.decodeFromString(
            deserializer = serializer,
            string = storedData
        )
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