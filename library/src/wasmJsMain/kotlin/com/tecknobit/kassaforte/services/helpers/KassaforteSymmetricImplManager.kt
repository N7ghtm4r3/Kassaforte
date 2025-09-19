package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.kassaforte.enums.ExportFormat.RAW
import com.tecknobit.kassaforte.helpers.IndexedDBManager
import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey
import kotlinx.coroutines.launch

internal class KassaforteSymmetricImplManager : KassaforteServiceImplManager<CryptoKey>() {

    override fun store(
        alias: String,
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
                keyData = key
            )
        }
    }

}