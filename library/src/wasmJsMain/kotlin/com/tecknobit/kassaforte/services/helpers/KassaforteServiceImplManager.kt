@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.kassaforte.helpers.IndexedDBManager
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.wrappers.crypto.key.RawCryptoKey
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.KeyGenSpec
import com.tecknobit.kassaforte.wrappers.crypto.subtleCrypto

internal class KassaforteServiceImplManager : KassaforteServiceManager<RawCryptoKey> {

    private val subtleCrypto = subtleCrypto()

    fun checkIfAliasAvailable(
        alias: String,
        onNotAvailable: () -> Unit,
        onAvailable: () -> Unit,
    ) {
        IndexedDBManager.checkIfAliasExists(
            alias = alias,
            onKeyExists = { onNotAvailable() },
            onKeyNotFound = { onAvailable() }
        )
    }

    @Assembler
    fun resolveUsages(
        purposes: KeyPurposes,
    ): JsArray<JsString> {
        val keyUsages = mutableListOf<String>()
        if (purposes.canEncrypt)
            keyUsages.add("encrypt")
        if (purposes.canDecrypt)
            keyUsages.add("decrypt")
        if (purposes.canSign)
            keyUsages.add("sign")
        if (purposes.canVerify)
            keyUsages.add("verify")
        if (purposes.canWrapKey)
            keyUsages.add("wrapKey")
        if (purposes.canAgree)
            keyUsages.add("deriveKey")
        if (keyUsages.isEmpty())
            throw IllegalStateException("Key usages not valid")
        return keyUsages.map { it.toJsString() }.toJsArray()
    }

    fun generateKey(
        genSpec: KeyGenSpec,
        usages: JsArray<JsString>,
    ) {
        subtleCrypto.generateKey(
            algorithm = genSpec,
            extractable = true,
            keyUsages = usages
        )
    }

    override fun isAliasTaken(
        alias: String,
    ): Boolean = true

    override fun retrieveKey(
        alias: String,
    ): RawCryptoKey {
        TODO()
    }

    override fun removeKey(
        alias: String,
    ) {
        TODO()
    }

}