@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.helpers

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey
import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKeyPair
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.KeyGenSpec
import com.tecknobit.kassaforte.wrappers.indexeddb.*
import com.tecknobit.kassaforte.wrappers.indexeddb.TransactionMode.READONLY
import com.tecknobit.kassaforte.wrappers.indexeddb.TransactionMode.READ_WRITE_MODE
import com.tecknobit.kassaforte.wrappers.indexeddb.requests.IDBOpenDBRequest
import com.tecknobit.kassaforte.wrappers.indexeddb.requests.IDBRequest
import org.khronos.webgl.ArrayBuffer
import org.w3c.dom.events.Event
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
        exportedKey: ArrayBuffer,
    ) {
        useIndexedDB(
            onReady = {
                val objectStore = obtainObjectStore(
                    transactionMode = READ_WRITE_MODE
                )
                objectStore.put(
                    item = buildRawKey(
                        alias = alias,
                        key = exportedKey.toEncodedKey(),
                        algorithm = key.algorithm,
                        extractable = key.extractable,
                        usages = key.usages
                    )
                )
            }
        )
    }

    fun addKeyPair(
        alias: String,
        algorithm: KeyGenSpec,
        keyPair: CryptoKeyPair,
        privateKey: ArrayBuffer,
        publicKey: ArrayBuffer,
    ) {
        useIndexedDB(
            onReady = {
                val objectStore = obtainObjectStore(
                    transactionMode = READ_WRITE_MODE
                )
                val privateKeyData = keyPair.privateKey
                objectStore.put(
                    item = buildRawKeyPair(
                        alias = alias,
                        algorithm = algorithm,
                        extractable = privateKeyData.extractable,
                        privateKey = privateKey.toEncodedKey(),
                        publicKey = publicKey.toEncodedKey(),
                        usages = privateKeyData.usages,
                        publicKeyUsages = keyPair.publicKey.usages
                    )
                )
            }
        )
    }

    @Returner
    private fun ArrayBuffer.toEncodedKey(): String {
        val keyBytes = this.toByteArray()
        return Base64.encode(keyBytes)
    }

    fun checkIfAliasExists(
        alias: String,
        onKeyExists: (Event) -> Unit,
        onKeyNotFound: (Event) -> Unit,
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
                request.onerror = { event -> onKeyNotFound(event) }
            }
        )
    }

    fun <K : CryptoKey> getAndUseKeyData(
        alias: String,
        onSuccess: (Event, K) -> Unit,
        onError: (Event) -> Unit = { throw RuntimeException(it.type) },
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
                        val rawKey = result.unsafeCast<K>()
                        onSuccess(event, rawKey)
                    }
                }
                request.onerror = { event -> onError(event) }
            }
        )
    }

    fun removeKey(
        alias: String,
    ) {
        useIndexedDB(
            onReady = {
                val objectStore = obtainObjectStore(
                    transactionMode = READ_WRITE_MODE
                )
                objectStore.delete(
                    key = alias
                )
            }
        )
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
    private fun <T> Event.getResult(): T? {
        return target?.unsafeCast<IDBRequest>()?.result?.unsafeCast()
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
    (alias, key, algorithm, extractable, usages) => (
        {
            alias: alias,
            key: key,
            algorithm: algorithm,
            extractable: extractable,
            usages: usages
        }
    )
    """
)
@Assembler
private external fun buildRawKey(
    alias: String,
    key: String,
    algorithm: KeyGenSpec,
    extractable: Boolean,
    usages: JsArray<JsString>,
): JsAny

@JsFun(
    """
    (alias, algorithm, extractable, publicKey, privateKey, usages, publicKeyUsages) => (
        {
            alias: alias,
            algorithm: algorithm,
            extractable: extractable,
            publicKey: publicKey,
            privateKey: privateKey,
            usages: usages,
            publicKeyUsages: publicKeyUsages
        }
    )
    """
)
@Assembler
private external fun buildRawKeyPair(
    alias: String,
    algorithm: KeyGenSpec,
    extractable: Boolean,
    publicKey: String,
    privateKey: String,
    usages: JsArray<JsString>,
    publicKeyUsages: JsArray<JsString>,
): JsAny