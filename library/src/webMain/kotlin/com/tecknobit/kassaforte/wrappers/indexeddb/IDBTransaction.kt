@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.indexeddb

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny

/**
 * The `TransactionMode` interface wraps the native [modes](https://developer.mozilla.org/en-US/docs/Web/API/IDBTransaction#mode_constants)
 *
 * @param value The native value of the transaction mode
 *
 * @author Tecknobit - N7ghtm4r3
 */
enum class TransactionMode(
    val value: String,
) {

    /**
     * `READ_WRITE_MODE` allows data to be read but not changed
     */
    READ_WRITE_MODE("readwrite"),

    /**
     * `READONLY` allows reading and writing of data in existing data stores to be changed
     */
    READONLY("readonly")

}

/**
 * The `IDBTransaction` interface wraps the native [IDBTransaction](https://developer.mozilla.org/en-US/docs/Web/API/IDBTransaction)
 * object
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see JsAny
 */
external interface IDBTransaction : JsAny {

    /**
     * Method used to get an object store object
     *
     * @param name The name of the object store to get
     *
     * @return the requested object store as [com.tecknobit.kassaforte.wrappers.indexeddb.IDBObjectStore]
     */
    fun objectStore(
        name: String,
    ): IDBObjectStore

}