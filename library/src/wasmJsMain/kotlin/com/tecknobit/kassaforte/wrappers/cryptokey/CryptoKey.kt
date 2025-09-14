package com.tecknobit.kassaforte.wrappers.cryptokey

internal external interface CryptoKey : JsAny {

    val type: String

    val extractable: Boolean

    val algorithm: KeyGenSpec

    val usages: JsArray<JsString>

}