@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.indexeddb.requests

import com.tecknobit.kassaforte.wrappers.indexeddb.IDBDatabase
import org.w3c.dom.events.Event

external interface IDBRequest : JsAny {

    val result: IDBDatabase

    var onsuccess: ((Event) -> Unit)?

    var onerror: ((Event) -> Unit)?

}