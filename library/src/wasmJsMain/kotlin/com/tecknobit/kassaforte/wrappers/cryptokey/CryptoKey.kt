@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.cryptokey

external interface CryptoKey : JsAny {

    val extractable: Boolean

    val algorithm: KeyGenSpec

    val usages: JsArray<JsString>

}