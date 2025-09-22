@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.indexeddb

import com.tecknobit.kassaforte.wrappers.indexeddb.requests.IDBOpenDBRequest

/**
 * The `IndexedDB` interface wraps the native [IndexedDB](https://developer.mozilla.org/en-US/docs/Web/API/IndexedDB)
 * object
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see JsAny
 */
external interface IndexedDB : JsAny {

    /**
     * Method used to open a connection with the indexedDB
     *
     * @param name The name of the database to open connection
     *
     * @return the open request as [IDBOpenDBRequest]
     */
    fun open(
        name: String,
    ): IDBOpenDBRequest

}

/**
 * Method used to obtain an instance of [IndexedDB]
 *
 * @return an indexedDb instance as [IndexedDB]
 */
@JsFun("() => window.indexedDB")
external fun indexedDb(): IndexedDB