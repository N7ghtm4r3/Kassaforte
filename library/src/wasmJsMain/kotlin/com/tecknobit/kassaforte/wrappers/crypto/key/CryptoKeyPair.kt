@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.crypto.key

external interface CryptoKeyPair : JsAny {

    val privateKey: CryptoKey

    val publicKey: CryptoKey

}