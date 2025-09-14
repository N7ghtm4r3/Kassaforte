@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.indexeddb

internal external object IndexedDb {

    fun open(
        name: String,
    ): IDBOpenDBRequest

}

@JsFun("() => window.indexedDb")
internal external fun indexedDb(): IndexedDb