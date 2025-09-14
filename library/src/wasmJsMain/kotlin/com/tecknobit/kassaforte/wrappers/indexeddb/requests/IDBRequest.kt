package com.tecknobit.kassaforte.wrappers.indexeddb.requests

import com.tecknobit.kassaforte.wrappers.indexeddb.IDBDatabase

external interface IDBRequest : JsAny {


    val result: IDBDatabase

}