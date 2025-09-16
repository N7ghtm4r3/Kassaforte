@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.crypto.params

sealed external interface AesParams : JsAny {

    val name: String

}