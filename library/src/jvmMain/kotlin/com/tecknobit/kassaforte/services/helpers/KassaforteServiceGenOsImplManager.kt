package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.kassaforte.Kassaforte
import com.tecknobit.kassaforte.key.usages.KeyDetailsSheet
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64

/**
 * The `KassaforteServiceGenOsImplManager` class allows to perform operations that [com.tecknobit.kassaforte.services.impls.KassaforteSymmetricServiceImpl]
 * and [com.tecknobit.kassaforte.services.impls.KassaforteAsymmetricServiceImpl] have in common
 *
 * It is particularly useful to avoid to break the `expect/actual` implementation and clean implement shared code avoiding
 * duplication.
 *
 * This particular service manager is adopted when the OS where the application is running on is any distros `Linux` or `macOs`
 * operating system
 *
 * @property serializer The serializer to use to correctly serialize a key info object from the stored data
 *
 * @param KI The type of the key info used by the service
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteServiceManager
 * @see KassaforteServiceImplManager
 */
internal class KassaforteServiceGenOsImplManager<KI : KeyDetailsSheet<*>>(
    serializer: KSerializer<KI>,
) : KassaforteServiceImplManager<KI>(
    serializer = serializer
) {

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
     * Method used to store the data of the generated key
     *
     * @param alias The alias which identify the key
     * @param keyInfo The extra information of the generated key to store
     */
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

    /**
     * Method used to retrieve from the secure storage the specified key
     *
     * @param alias The alias used to store the key
     *
     * @return the key as [KI]
     */
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