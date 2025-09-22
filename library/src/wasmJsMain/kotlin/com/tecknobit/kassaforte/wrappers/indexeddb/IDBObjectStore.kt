@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.indexeddb

import com.tecknobit.kassaforte.wrappers.indexeddb.requests.IDBRequest

/**
 * The `IDBObjectStore` interface wraps the native [IDBObjectStore](https://developer.mozilla.org/en-US/docs/Web/API/IDBObjectStore)
 * object
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see JsAny
 */
external interface IDBObjectStore : JsAny {

    /**
     * Method used to create an index inside the object store
     *
     * @param name The name of the next to create
     * @param keyPath The key path for the index to use
     */
    fun createIndex(
        name: String,
        keyPath: String,
    )

    /**
     * Method used to get an item from the object store
     *
     * @param key The key or key range that identifies the record to be retrieved
     *
     * @return the request to correctly handle the get request as [IDBRequest]
     */
    fun get(
        key: String,
    ): IDBRequest

    /**
     * Method used to put an item inside the object store
     *
     * @param item The data of the item to put
     *
     * @return the request to correctly handle the put request as [IDBRequest]
     */
    fun put(
        item: JsAny,
    ): IDBRequest

    /**
     * Method used to delete an item from the object store
     *
     * @param key The key of the record to be deleted
     *
     * @return the request to correctly handle the delete request as [IDBRequest]
     */
    fun delete(
        key: String,
    ): IDBRequest

}