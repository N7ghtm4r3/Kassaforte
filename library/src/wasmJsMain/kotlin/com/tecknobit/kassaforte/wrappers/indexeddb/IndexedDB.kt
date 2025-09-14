@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.indexeddb

import com.tecknobit.kassaforte.wrappers.indexeddb.requests.IDBOpenDBRequest

external interface IndexedDB {

    fun open(
        name: String,
    ): IDBOpenDBRequest

}

@JsFun("() => window.indexedDB")
external fun indexedDb(): IndexedDB