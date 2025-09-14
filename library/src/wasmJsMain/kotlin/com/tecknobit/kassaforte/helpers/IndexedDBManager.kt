@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.helpers

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.wrappers.indexeddb.IDBDatabase
import com.tecknobit.kassaforte.wrappers.indexeddb.IndexedDB
import com.tecknobit.kassaforte.wrappers.indexeddb.indexedDb
import com.tecknobit.kassaforte.wrappers.indexeddb.requests.IDBOpenDBRequest
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import kotlin.io.encoding.Base64

object IndexedDBManager {

    private const val DATABASE_NAME = "Kassaforte"

    private const val OBJECT_STORAGE_NAME = "keys"

    private const val ALIAS_KEY = "alias"

    private const val ALIAS_IDX = "${ALIAS_KEY}_idx"

    private const val READ_WRITE_MODE = "readwrite"

    private val indexedDB = indexedDb()

    init {
        val request = indexedDB.openDB()
        request.onupgradeneeded = { event ->
            val db = event.target!!.unsafeCast<IDBOpenDBRequest>().result
            db.createMainObjectStore()
        }
    }

    private fun IDBDatabase.createMainObjectStore() {
        if (!objectStoreNames.contains(DATABASE_NAME)) {
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

    fun addKey(
        alias: String,
        key: ArrayBuffer,
    ) {
        val request = indexedDB.openDB()
        request.onsuccess = { event ->
            val db = event.target!!.unsafeCast<IDBOpenDBRequest>().result
            val transaction = db.transaction(
                storeNames = OBJECT_STORAGE_NAME,
                mode = READ_WRITE_MODE
            )
            val objectStore = transaction.objectStore(
                name = OBJECT_STORAGE_NAME
            )
            objectStore.put(
                item = buildItem(
                    alias = alias,
                    key = key.toEncodedKey()
                )
            )
        }
    }

    @Returner
    private fun ArrayBuffer.toEncodedKey(): String {
        val uInt8 = Uint8Array(this)
        val keyBytes = ByteArray(uInt8.length) { index -> uInt8[index] }
        return Base64.encode(keyBytes)
    }

    private fun IndexedDB.openDB(): IDBOpenDBRequest {
        return open(
            name = DATABASE_NAME
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
    (alias, key) => (
        {
            alias: alias,
            key: key
        }
    )
    """
)
@Assembler
private external fun buildItem(
    alias: String,
    key: String,
): JsAny