package com.tecknobit.kassaforte.wrappers.indexeddb

enum class TransactionMode(
    val value: String,
) {

    READ_WRITE_MODE("readwrite"),

    READONLY("readonly")

}

external interface IDBTransaction {

    fun objectStore(
        name: String,
    ): IDBObjectStore

}