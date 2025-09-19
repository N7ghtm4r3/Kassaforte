package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.kassaforte.enums.ExportFormat.RAW
import com.tecknobit.kassaforte.helpers.IndexedDBManager
import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.KeyGenSpec
import com.tecknobit.kassaforte.wrappers.crypto.key.raw.RawCryptoKey
import kotlinx.coroutines.launch

internal class KassaforteSymmetricImplManager : KassaforteServiceImplManager<CryptoKey, RawCryptoKey>() {

    override fun store(
        alias: String,
        algorithm: KeyGenSpec,
        result: CryptoKey,
    ) {
        managerScope.launch {
            val key = exportKey(
                key = result,
                format = RAW
            )
            IndexedDBManager.addKey(
                alias = alias,
                key = result,
                exportedKey = key
            )
        }
    }

}