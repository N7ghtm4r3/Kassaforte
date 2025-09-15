@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.indexeddb.requests

import org.w3c.dom.events.Event

external interface IDBRequest : JsAny {

    val result: JsAny?

    var onsuccess: ((Event) -> Unit)?

    var onerror: ((Event) -> Unit)?

}