@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.crypto.key.genspec

external interface KeyGenSpec : JsAny {

    val name: String

}

