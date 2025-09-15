@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.crypto.params

external interface AesParams : JsAny {

    val name: String

}