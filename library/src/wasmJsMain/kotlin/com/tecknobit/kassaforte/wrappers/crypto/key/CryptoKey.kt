@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.crypto.key

import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.KeyGenSpec

external interface CryptoKey : JsAny {

    val extractable: Boolean

    val algorithm: KeyGenSpec

    val usages: JsArray<JsString>

}