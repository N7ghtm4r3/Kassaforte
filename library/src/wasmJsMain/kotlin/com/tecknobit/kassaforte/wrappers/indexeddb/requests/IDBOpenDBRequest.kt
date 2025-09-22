package com.tecknobit.kassaforte.wrappers.indexeddb.requests

import org.w3c.dom.events.Event

/**
 * The `IDBOpenDBRequest` interface wraps the native [IDBOpenDBRequest](https://developer.mozilla.org/en-US/docs/Web/API/IDBOpenDBRequest)
 * object
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see JsAny
 * @see IDBRequest
 */
external interface IDBOpenDBRequest : IDBRequest {

    /**
     * `onupgradeneeded` Fired when an attempt was made to open a database with a version number higher than its current version
     */
    var onupgradeneeded: ((Event) -> Unit)?

}