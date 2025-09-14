package com.tecknobit.kassaforte.wrappers.indexeddb

external interface IDBTransaction {

    fun objectStore(
        name: String,
    ): IDBObjectStore

}