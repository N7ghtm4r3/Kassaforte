@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.indexeddb

import com.tecknobit.kassaforte.wrappers.indexeddb.requests.IDBRequest

external interface IDBObjectStore : JsAny {

    fun createIndex(
        name: String,
        keyPath: String,
    )

    fun get(
        key: String,
    ): IDBRequest

    fun put(
        item: JsAny,
    ): IDBRequest

}