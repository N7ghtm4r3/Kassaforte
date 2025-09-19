@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.enums.ExportFormat
import com.tecknobit.kassaforte.helpers.IndexedDBManager
import com.tecknobit.kassaforte.helpers.prepareToDecrypt
import com.tecknobit.kassaforte.helpers.prepareToEncrypt
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.KeyGenSpec
import com.tecknobit.kassaforte.wrappers.crypto.params.EncryptionParams
import com.tecknobit.kassaforte.wrappers.crypto.subtleCrypto
import kotlinx.coroutines.*
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import kotlin.coroutines.resume
import kotlin.io.encoding.Base64

internal abstract class KassaforteServiceImplManager<K : JsAny, RK : CryptoKey> : KassaforteServiceManager<K> {

    private val subtleCrypto = subtleCrypto()

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
            algorithm = genSpec,
            result = result
        )
    }

    protected abstract fun store(
        alias: String,
        algorithm: KeyGenSpec,
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

    suspend fun retrieveKeyData(
        alias: String,
    ): RK {
        return suspendCancellableCoroutine { continuation ->
            IndexedDBManager.getAndUseKeyData<RK>(
                alias = alias,
                onSuccess = { _, rawKeyData -> continuation.resume(rawKeyData) },
                onError = { eventError -> throw RuntimeException(eventError.type) }
            )
        }
    }

    suspend fun useKey(
        rawKey: String,
        rawKeyData: CryptoKey,
        usages: JsArray<JsString> = rawKeyData.usages,
        format: ExportFormat,
        usage: suspend (CryptoKey) -> String,
    ): String {
        println(rawKeyData.algorithm.name)
        val keyData = rawKey.toDecodedKeyData()
        val key: CryptoKey = subtleCrypto.importKey(
            format = format.value,
            keyData = keyData,
            algorithm = rawKeyData.algorithm,
            extractable = rawKeyData.extractable,
            keyUsages = usages
        ).await()
        return usage(key)
    }

    @Returner
    private fun String.toDecodedKeyData(): ArrayBuffer {
        val encodedKeyData = Base64.decode(this)
        val uint8Array = Uint8Array(encodedKeyData.size)
        val mappedSourceArray = encodedKeyData
            .map { (it.toInt() and 0xFF).toJsNumber() }
            .toJsArray()
        uint8Array.set(
            array = mappedSourceArray
        )
        return uint8Array.buffer
    }

    suspend fun encrypt(
        algorithm: EncryptionParams,
        key: CryptoKey,
        data: Any,
    ): ArrayBuffer {
        return subtleCrypto.encrypt(
            algorithm = algorithm,
            key = key,
            data = data.prepareToEncrypt()
        ).await()
    }

    suspend fun decrypt(
        algorithm: EncryptionParams,
        key: CryptoKey,
        data: Any,
    ): ArrayBuffer {
        return subtleCrypto.decrypt(
            algorithm = algorithm,
            key = key,
            data = data.prepareToDecrypt()
        ).await()
    }

    override fun removeKey(
        alias: String,
    ) {
        IndexedDBManager.removeKey(
            alias = alias
        )
    }

    override fun isAliasTaken(
        alias: String,
    ): Boolean = true

    override fun retrieveKey(
        alias: String,
    ): K = TODO("UNUSED")

}