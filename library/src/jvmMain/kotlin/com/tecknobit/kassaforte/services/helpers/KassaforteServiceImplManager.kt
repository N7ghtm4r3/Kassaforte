@file:OptIn(ExperimentalStdlibApi::class)

package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.equinoxcore.annotations.Structure
import com.tecknobit.equinoxcore.enums.OperatingSystem
import com.tecknobit.equinoxcore.util.isRunningOn
import com.tecknobit.kassaforte.key.usages.KeyDetailsSheet
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.security.Key
import kotlin.io.encoding.Base64

/**
 * The `KassaforteServiceImplManager` class allows to perform operations that [com.tecknobit.kassaforte.services.impls.KassaforteSymmetricServiceImpl]
 * and [com.tecknobit.kassaforte.services.impls.KassaforteAsymmetricServiceImpl] have in common
 *
 * It is particularly useful to avoid to break the `expect/actual` implementation and clean implement shared code avoiding
 * duplication
 *
 * @property serializer The serializer to use to correctly serialize a key info object from the stored data
 *
 * @param KI The type of the key info used by the service
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteServiceManager
 */
@Structure
internal abstract class KassaforteServiceImplManager<KI : KeyDetailsSheet<*>> internal constructor(
    protected val serializer: KSerializer<KI>,
) : KassaforteServiceManager<KI> {

    internal companion object {

        /**
         * Method used to encode a key in [Base64] format
         *
         * @return the encoded key as [String]
         */
        @Returner
        fun Key.encode64(): String {
            return Base64.encode(this.encoded)
        }

        /**
         * Method used to obtain the correct instance of the manager based on the OS where the application is running
         *
         * @param serializer The serializer to use to correctly serialize a key info object from the stored data
         *
         * @param KI The type of the key info used by the service
         *
         * @return the correct instance of the manager as [KassaforteServiceImplManager]
         */
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

    /**
     * Method used to store the data of the generated key
     *
     * @param alias The alias which identify the key
     * @param keyInfo The extra information of the generated key to store
     */
    abstract fun storeKeyData(
        alias: String,
        keyInfo: KI,
    )

    /**
     * Method used to format the data of the key
     *
     * @param keyInfo The extra information of the generated key
     * @param encode64 Whether is necessary to encode the key data in [Base64] format
     *
     * @return the data of the key formatted as [String]
     */
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