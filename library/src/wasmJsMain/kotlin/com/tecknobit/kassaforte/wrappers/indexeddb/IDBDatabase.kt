@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.indexeddb

import org.w3c.dom.DOMTokenList

external interface IDBDatabase : JsAny {

    val objectStoreNames: DOMTokenList

    fun createObjectStore(
        name: String,
        options: JsAny,
    ): IDBObjectStore

    fun transaction(
        storeNames: String,
        mode: String,
    ): IDBTransaction

}