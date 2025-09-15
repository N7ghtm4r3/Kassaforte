package com.tecknobit.kassaforte.wrappers.indexeddb.requests

import org.w3c.dom.events.Event

external interface IDBOpenDBRequest : IDBRequest {

    var onupgradeneeded: ((Event) -> Unit)?

}