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

/**
 * The `IndexedDBManager` allows the management of the native [IndexedDB](https://developer.mozilla.org/en-US/docs/Web/API/IndexedDB_API) APIs
 * to secure store the keys and to retrieve the keys when needed
 *
 * @author Tecknobit - N7ghtm4r3
 */
internal object IndexedDBManager {

    /**
     * `DATABASE_NAME` the name of the database to create to store the keys
     */
    private const val DATABASE_NAME = "Kassaforte"

    /**
     * `OBJECT_STORAGE_NAME` the name of the object storage where the keys will be stored
     */
    private const val OBJECT_STORAGE_NAME = "keys"

    /**
     * `ALIAS_KEY` the constant for the `alias` value
     */
    private const val ALIAS_KEY = "alias"

    /**
     * `ALIAS_IDX` the constant for the `alias_idx` value
     */
    private const val ALIAS_IDX = "${ALIAS_KEY}_idx"

    /**
     * `indexedDB` the instance of the indexed database
     */
    private lateinit var indexedDB: IDBDatabase

    init {
        /**
         * Will be created the database if not exists yet
         */
        useIndexedDB()
    }

    /**
     * Method used to secure store a new generated symmetric key
     *
     * @param alias The alias used to identify the key
     * @param key The generated key to store
     * @param exportedKey The exported key to store
     */
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

    /**
     * Method used to secure store a new generated key pair of asymmetric algorithm
     *
     * @param alias The alias used to identify the key
     * @param algorithm The gen spec of the generated key pair
     * @param keyPair The generated key pair to store
     * @param privateKey The exported private key to store
     * @param publicKey The exported public key to store
     */
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

    /**
     * Method used to convert an [ArrayBuffer] into an encoded key
     *
     * @return the encoded key in [Base64] format as [String]
     */
    @Returner
    private fun ArrayBuffer.toEncodedKey(): String {
        val keyBytes = this.toByteArray()
        return Base64.encode(keyBytes)
    }

    /**
     * Method used to check whether the alias already stored in the [indexedDB]
     *
     * @param alias The alias used to identify the key
     * @param onAliasExists The callback to invoke whether the alias already exists
     * @param onAliasNotFound The callback to invoke whether the alias does not exist
     */
    fun checkIfAliasExists(
        alias: String,
        onAliasExists: (Event) -> Unit,
        onAliasNotFound: (Event) -> Unit,
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
                        onAliasNotFound(event)
                    else
                        onAliasExists(event)
                }
                request.onerror = { event -> onAliasNotFound(event) }
            }
        )
    }

    /**
     * Method used to retrieve and then use the key data previously retrieved
     *
     * @param alias The alias used to identify the key
     * @param onSuccess The callback to invoke when the key has been successfully retrieved
     * @param onError The callback to invoke when an error occurred during the key retrieval
     * @param onKeyNotFound The callback to invoke when the specified key does not exist
     */
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

    /**
     * Method used to remove from the [indexedDB] the specified key
     *
     * @param alias The alias used to identify the key to remove
     */
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

    /**
     * Utility method that allows to use the [indexedDB] instance safely, so when that instance has been correctly opened
     *
     * @param onReady The callback to execute when the [indexedDB] instance is ready to be used
     */
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

    /**
     * Method used to open an instance of the [IndexedDB]
     *
     * @return the created request as [IDBOpenDBRequest]
     */
    private fun IndexedDB.openDB(): IDBOpenDBRequest {
        return open(
            name = DATABASE_NAME
        )
    }

    /**
     * Method used to create the [OBJECT_STORAGE_NAME] if not already exists
     */
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

    /**
     * Method used to get a result from an [Event]
     *
     * @param T The type of the result
     *
     * @return the result from an event as nullable [T]
     */
    @Returner
    private fun <T> Event.getResult(): T? {
        return target?.unsafeCast<IDBRequest>()?.result?.unsafeCast()
    }

    /**
     * Method used to obtain the [OBJECT_STORAGE_NAME] object to work on it
     *
     * @param transactionMode The mode of the transaction to work with the [IDBObjectStore]
     *
     * @return the requested object store as [IDBObjectStore]
     */
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

/**
 * Method used to assemble the options for an [IDBObjectStore]
 *
 * @return the options for an object store as [JsAny]
 */
@JsFun(
    """
    () => ({ keyPath: 'alias' })
    """
)
@Assembler
private external fun objectStoreOptions(): JsAny

/**
 * Method used to assemble the item which represents the raw key to add into the [IndexedDB]
 *
 * @param alias The alias used to identify the key
 * @param key The generated key to store
 * @param algorithm The algorithm the key will use
 * @param extractable Whether the key is extractable
 * @param usages The usages assigned to the key
 *
 * @return the assembled item as [JsAny]
 */
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

/**
 * Method used to assemble the item which represents the raw key pair to add into the [IndexedDB]
 *
 * @param alias The alias used to identify the key
 * @param algorithm The algorithm the key will use
 * @param extractable Whether the key is extractable
 * @param privateKey The exported private key to store
 * @param publicKey The exported public key to store
 * @param usages The usages assigned to the private key
 * @param publicKeyUsages The usages assigned to the public key
 *
 * @return the assembled item as [JsAny]
 */
@JsFun(
    """
    (alias, algorithm, extractable, privateKey, publicKey, usages, publicKeyUsages) => (
        {
            alias: alias,
            algorithm: algorithm,
            extractable: extractable,
            privateKey: privateKey,
            publicKey: publicKey,
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
    privateKey: String,
    publicKey: String,
    usages: JsArray<JsString>,
    publicKeyUsages: JsArray<JsString>,
): JsAny