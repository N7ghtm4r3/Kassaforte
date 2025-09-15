@file:OptIn(ExperimentalWasmJsInterop::class, ExperimentalAtomicApi::class)

package com.tecknobit.kassaforte.helpers

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.wrappers.RAW_EXPORT_FORMAT
import com.tecknobit.kassaforte.wrappers.cryptokey.CryptoKey
import com.tecknobit.kassaforte.wrappers.cryptokey.KeyGenSpec
import com.tecknobit.kassaforte.wrappers.cryptokey.RawCryptoKey
import com.tecknobit.kassaforte.wrappers.indexeddb.*
import com.tecknobit.kassaforte.wrappers.indexeddb.TransactionMode.READONLY
import com.tecknobit.kassaforte.wrappers.indexeddb.TransactionMode.READ_WRITE_MODE
import com.tecknobit.kassaforte.wrappers.indexeddb.requests.IDBOpenDBRequest
import com.tecknobit.kassaforte.wrappers.indexeddb.requests.IDBRequest
import com.tecknobit.kassaforte.wrappers.subtleCrypto
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.dom.events.Event
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.io.encoding.Base64

object IndexedDBManager {

    private const val DATABASE_NAME = "Kassaforte"

    private const val OBJECT_STORAGE_NAME = "keys"

    private const val ALIAS_KEY = "alias"

    private const val ALIAS_IDX = "${ALIAS_KEY}_idx"

    private lateinit var indexedDB: IDBDatabase

    init {
        useIndexedDB()
    }

    fun addKey(
        alias: String,
        key: CryptoKey,
        keyData: ArrayBuffer,
    ) {
        useIndexedDB(
            onReady = {
                val objectStore = obtainObjectStore(
                    transactionMode = READ_WRITE_MODE
                )
                objectStore.put(
                    item = buildItem(
                        alias = alias,
                        keyData = keyData.toEncodedKey(),
                        algorithm = key.algorithm,
                        extractable = key.extractable,
                        keyUsages = key.usages
                    )
                )
            }
        )
    }

    @Returner
    private fun ArrayBuffer.toEncodedKey(): String {
        val uInt8 = Uint8Array(this)
        val keyBytes = ByteArray(uInt8.length) { index -> uInt8[index] }
        return Base64.encode(keyBytes)
    }

    fun checkIfKeyExists(
        alias: String,
        onKeyExists: (Event) -> Unit,
        onError: (Event) -> Unit,
        onKeyNotFound: (Event) -> Unit = onError,
    ) {
        useIndexedDB(
            onReady = {
                val objectStore = obtainObjectStore(
                    transactionMode = READONLY
                )
                val request = objectStore.get(
                    key = alias
                )
                request.onsuccess = { event ->
                    val result: JsAny? = request.result
                    if (result == null)
                        onKeyNotFound(event)
                    else
                        onKeyExists(event)
                }
                request.onerror = { event -> onError(event) }
            }
        )
    }

    fun useKey(
        alias: String,
        onSuccess: (Event, CryptoKey) -> Unit,
        onError: (Event) -> Unit,
        onKeyNotFound: (Event) -> Unit = onError,
    ) {
        useIndexedDB(
            onReady = {
                val objectStore = obtainObjectStore(
                    transactionMode = READONLY
                )
                val request = objectStore.get(
                    key = alias
                )
                request.onsuccess = { event ->
                    val result: JsAny? = request.result
                    if (result == null)
                        onKeyNotFound(event)
                    else {
                        val rawKey = result.unsafeCast<RawCryptoKey>()
                        val keyData = rawKey.keyData.toDecodedKeyData()
                        val subtleCrypto = subtleCrypto()
                        MainScope().launch {
                            val key: CryptoKey = subtleCrypto.importKey(
                                format = RAW_EXPORT_FORMAT,
                                keyData = keyData,
                                algorithm = rawKey.algorithm,
                                extractable = rawKey.extractable,
                                keyUsages = rawKey.usages
                            ).await()
                            onSuccess(event, key)
                        }
                    }
                }
                request.onerror = { event -> onError(event) }
            }
        )
    }

    @Returner
    private fun String.toDecodedKeyData(): ArrayBuffer {
        val encodedKeyData = Base64.decode(this)
        val uint8Array = Uint8Array(encodedKeyData.size)
        return uint8Array.buffer
    }

    private fun useIndexedDB(
        onReady: (() -> Unit)? = null,
    ) {
        if (::indexedDB.isInitialized) {
            onReady?.invoke()
            return
        }
        val request = indexedDb().openDB()
        request.onsuccess = { event ->
            indexedDB = event.getResult()!!
            onReady?.invoke()
        }
        request.onupgradeneeded = { event ->
            indexedDB = event.getResult()!!
            indexedDB.createMainObjectStore()
        }
        request.onerror = {
            throw IllegalStateException("Could not store with Kassaforte in this context")
        }
    }

    private fun IndexedDB.openDB(): IDBOpenDBRequest {
        return open(
            name = DATABASE_NAME
        )
    }

    private fun IDBDatabase.createMainObjectStore() {
        if (!objectStoreNames.contains(OBJECT_STORAGE_NAME)) {
            val objectStore = createObjectStore(
                name = OBJECT_STORAGE_NAME,
                options = objectStoreOptions()
            )
            objectStore.createIndex(
                name = ALIAS_IDX,
                keyPath = ALIAS_KEY
            )
        }
    }

    @Returner
    private fun obtainObjectStore(
        transactionMode: TransactionMode,
    ): IDBObjectStore {
        val transaction = indexedDB.transaction(
            storeNames = OBJECT_STORAGE_NAME,
            mode = transactionMode.value
        )
        return transaction.objectStore(
            name = OBJECT_STORAGE_NAME
        )
    }

    @Returner
    private fun <T> Event.getResult(): T? {
        return target?.unsafeCast<IDBRequest>()?.result?.unsafeCast()
    }

}

@JsFun(
    """
    () => ({ keyPath: 'alias' })
    """
)
@Returner
private external fun objectStoreOptions(): JsAny

@JsFun(
    """
    (alias, keyData, algorithm, extractable, keyUsages) => (
        {
            alias: alias,
            keyData: keyData,
            algorithm: algorithm,
            extractable: extractable,
            keyUsages: keyUsages
        }
    )
    """
)
@Assembler
private external fun buildItem(
    alias: String,
    keyData: String,
    algorithm: KeyGenSpec,
    extractable: Boolean,
    keyUsages: JsArray<JsString>,
): JsAny