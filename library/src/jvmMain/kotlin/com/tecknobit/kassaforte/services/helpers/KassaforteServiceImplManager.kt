@file:OptIn(ExperimentalStdlibApi::class)

package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.equinoxcore.enums.OperatingSystem
import com.tecknobit.equinoxcore.util.isRunningOn
import com.tecknobit.kassaforte.key.usages.KeyDetailsSheet
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.security.Key
import kotlin.io.encoding.Base64

internal abstract class KassaforteServiceImplManager<KI : KeyDetailsSheet<*>> internal constructor(
    protected val serializer: KSerializer<KI>,
) : KassaforteServiceManager<KI> {

    internal companion object {

        fun Key.encode64(): String {
            return Base64.encode(this.encoded)
        }

        @Returner
        fun <KI : KeyDetailsSheet<*>> getInstance(
            serializer: KSerializer<KI>,
        ): KassaforteServiceImplManager<KI> {
            return when (isRunningOn()) {
                OperatingSystem.WINDOWS -> KassaforteServiceWinImplManager(
                    serializer = serializer
                )

                else -> KassaforteServiceGenOsImplManager(
                    serializer = serializer
                )
            }
        }

    }

    abstract fun storeKeyData(
        alias: String,
        keyInfo: KI,
    )

    @Returner
    protected fun formatKeyData(
        keyInfo: KI,
        encode64: Boolean = true,
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

}