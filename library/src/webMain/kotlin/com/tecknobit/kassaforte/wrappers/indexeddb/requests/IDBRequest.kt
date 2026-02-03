@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.indexeddb.requests

import org.w3c.dom.events.Event
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny

/**
 * The `IDBRequest` interface wraps the native [IDBRequest](https://developer.mozilla.org/en-US/docs/Web/API/IDBRequest)
 * object
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see JsAny
 */
external interface IDBRequest : JsAny {

    /**
     * `result` the result of the request
     */
    val result: JsAny?

    /**
     * `onsuccess` the callback invoked when the request was successful
     */
    var onsuccess: ((Event) -> Unit)?

    /**
     * `onsuccess` the callback invoked when the request was failed with an error
     */
    var onerror: ((Event) -> Unit)?

}