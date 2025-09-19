@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.crypto.key.raw

import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey

external interface RawCryptoKeyPair : CryptoKey {

    val alias: String

    val privateKey: String

    val publicKey: String

    val publicKeyUsages: JsArray<JsString>

}