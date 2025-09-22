package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.kassaforte.enums.ExportFormat.RAW
import com.tecknobit.kassaforte.helpers.IndexedDBManager
import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.KeyGenSpec
import com.tecknobit.kassaforte.wrappers.crypto.key.raw.RawCryptoKey
import kotlinx.coroutines.launch

/**
 * The `KassaforteSymmetricServiceManager` class allows to perform indexedb's operations on symmetric keys
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteServiceManager
 * @see KassaforteServiceImplManager
 * @see CryptoKey
 * @see RawCryptoKey
 */
internal class KassaforteSymmetricServiceManager : KassaforteServiceImplManager<CryptoKey, RawCryptoKey>() {

    /**
     * Method used to secure store a new generated symmetric key
     *
     * @param alias The alias used to identify the key
     * @param algorithm The gen spec of the key to generate
     * @param generatedKey The newly generated key
     */
    override fun store(
        alias: String,
        algorithm: KeyGenSpec,
        generatedKey: CryptoKey,
    ) {
        managerScope.launch {
            val key = exportKey(
                key = generatedKey,
                format = RAW
            )
            IndexedDBManager.addKey(
                alias = alias,
                key = generatedKey,
                exportedKey = key
            )
        }
    }

}