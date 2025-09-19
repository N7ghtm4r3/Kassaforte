package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.kassaforte.enums.ExportFormat.PKCS8
import com.tecknobit.kassaforte.enums.ExportFormat.SPKI
import com.tecknobit.kassaforte.helpers.IndexedDBManager
import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKeyPair
import kotlinx.coroutines.launch

internal class KassaforteAsymmetricImplManager : KassaforteServiceImplManager<CryptoKeyPair>() {

    override fun store(
        alias: String,
        result: CryptoKeyPair,
    ) {
        managerScope.launch {
            val privateKey = exportKey(
                key = result.privateKey,
                format = PKCS8
            )
            val publicKey = exportKey(
                key = result.publicKey,
                format = SPKI
            )
            IndexedDBManager.addKeyPair(
                alias = alias,
                keyPair = result,
                privateKey = privateKey,
                publicKey = publicKey
            )
        }
    }

}