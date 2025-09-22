@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.indexeddb

import org.w3c.dom.DOMTokenList

/**
 * The `IDBDatabase` interface wraps the native [IDBDatabase](https://developer.mozilla.org/en-US/docs/Web/API/IDBDatabase)
 * object
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see JsAny
 */
external interface IDBDatabase : JsAny {

    /**
     * `objectStoreNames` list of the names of the object stores currently in the connected database
     */
    val objectStoreNames: DOMTokenList

    /**
     * Method used to create a new object store
     *
     * @param name The name of the object store
     * @param options Extra options to create the object store
     *
     * @return the created object store as [IDBObjectStore]
     */
    fun createObjectStore(
        name: String,
        options: JsAny,
    ): IDBObjectStore

    /**
     * Method used to execute a transaction
     *
     * @param storeNames The names of object store that are in the scope of the new transaction
     * @param mode The type of access that can be performed in the transaction
     *
     * @return the created transaction as [IDBTransaction]
     */
    fun transaction(
        storeNames: String,
        mode: String,
    ): IDBTransaction

}