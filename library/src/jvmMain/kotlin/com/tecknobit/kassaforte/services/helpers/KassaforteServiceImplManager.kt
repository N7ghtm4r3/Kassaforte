@file:OptIn(InternalSerializationApi::class)

package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.Kassaforte
import com.tecknobit.kassaforte.key.usages.KeyDetailsSheet
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.security.Key
import kotlin.io.encoding.Base64

internal class KassaforteServiceImplManager<KI : KeyDetailsSheet<*>>(
    private val serializer: KSerializer<KI>,
) : KassaforteServiceManager<KI> {

    internal companion object {

        fun Key.encode64(): String {
            return Base64.encode(this.encoded)
        }

    }

    override fun isAliasTaken(
        alias: String,
    ): Boolean {
        val kassaforte = Kassaforte(alias)
        return kassaforte.unsuspendedWithdraw(
            key = alias
        ) != null
    }

    fun storeKeyData(
        alias: String,
        keyInfo: KI,
        encode64: Boolean = true,
    ) {
        val keyData = formatKeyData(
            keyInfo = keyInfo,
            encode64 = encode64
        )
        val kassaforte = Kassaforte(alias)
        kassaforte.safeguard(
            key = alias,
            data = keyData
        )
    }

    @Returner
    private fun formatKeyData(
        keyInfo: KI,
        encode64: Boolean,
    ): String {
        val encodedKeyInfo = Json.encodeToString(
            serializer = serializer,
            keyInfo
        )
        return if (encode64)
            Base64.encode(encodedKeyInfo.encodeToByteArray())
        else
            encodedKeyInfo
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
        val storedData = try {
            Base64.decode(encodedKeyData).decodeToString()
        } catch (e: IllegalArgumentException) {
            encodedKeyData
        }
        return Json.decodeFromString(serializer, storedData)
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