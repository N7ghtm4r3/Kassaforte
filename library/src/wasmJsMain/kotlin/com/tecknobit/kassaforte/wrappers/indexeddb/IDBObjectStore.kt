@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.indexeddb

import com.tecknobit.kassaforte.wrappers.indexeddb.requests.IDBRequest

external interface IDBObjectStore : JsAny {

    fun put(
        item: JsAny,
    ): IDBRequest

}