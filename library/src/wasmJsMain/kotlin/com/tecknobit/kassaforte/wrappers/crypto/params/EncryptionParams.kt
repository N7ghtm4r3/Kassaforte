@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.crypto.params

sealed external interface EncryptionParams : JsAny {

    val name: String

}