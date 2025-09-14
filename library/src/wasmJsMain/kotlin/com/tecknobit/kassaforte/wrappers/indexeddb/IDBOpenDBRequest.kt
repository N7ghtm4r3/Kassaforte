package com.tecknobit.kassaforte.wrappers.indexeddb

import org.w3c.dom.events.Event

internal external interface IDBOpenDBRequest {

    fun onSuccess(
        event: Event,
    )

}