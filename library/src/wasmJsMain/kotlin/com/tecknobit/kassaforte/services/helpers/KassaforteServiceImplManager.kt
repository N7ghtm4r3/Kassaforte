@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.enums.ExportFormat
import com.tecknobit.kassaforte.helpers.IndexedDBManager
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.KeyGenSpec
import com.tecknobit.kassaforte.wrappers.crypto.subtleCrypto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.khronos.webgl.ArrayBuffer

internal abstract class KassaforteServiceImplManager<K : JsAny> : KassaforteServiceManager<K> {

    protected val subtleCrypto = subtleCrypto()

    protected val managerScope = CoroutineScope(
        context = Dispatchers.Default
    )

    fun generateKey(
        alias: String,
        genSpec: () -> KeyGenSpec,
        purposes: KeyPurposes,
    ) {
        IndexedDBManager.checkIfAliasExists(
            alias = alias,
            onKeyExists = { return@checkIfAliasExists },
            onKeyNotFound = {
                managerScope.launch {
                    generateAndStore(
                        alias = alias,
                        genSpec = genSpec(),
                        usages = resolveUsages(
                            purposes = purposes
                        )
                    )
                }
            }
        )
    }

    // TODO: TO DOCU ABOUT THE COMBINATION WITH USAGES AND KEYGEN 
    @Assembler
    private fun resolveUsages(
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

    private suspend fun generateAndStore(
        alias: String,
        genSpec: KeyGenSpec,
        usages: JsArray<JsString>,
    ) {
        val result: K = subtleCrypto.generateKey(
            algorithm = genSpec,
            extractable = true,
            keyUsages = usages
        ).await()
        store(
            alias = alias,
            result = result
        )
    }

    protected abstract fun store(
        alias: String,
        result: K,
    )

    @Returner
    protected suspend fun exportKey(
        key: CryptoKey,
        format: ExportFormat,
    ): ArrayBuffer {
        return subtleCrypto.exportKey(
            format = format.value,
            key = key
        ).await()
    }

    override fun isAliasTaken(
        alias: String,
    ): Boolean = true

    override fun retrieveKey(
        alias: String,
    ): K {
        TODO()
    }

    override fun removeKey(
        alias: String,
    ) {
        IndexedDBManager.removeKey(
            alias = alias
        )
    }

}