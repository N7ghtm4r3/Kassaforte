@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.crypto.key.genspec

external interface RsaHashedKeyGenParams : KeyGenSpec {

    val modulusLength: Int

    val publicExponent: JsArray<JsNumber>

    val hash: String

}

