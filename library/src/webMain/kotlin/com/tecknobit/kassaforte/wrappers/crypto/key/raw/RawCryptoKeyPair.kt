@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.crypto.key.raw

import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsArray
import kotlin.js.JsString

/**
 * The `RawCryptoKeyPair` interface represents the data of a stored key pair
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see kotlin.js.JsAny
 * @see CryptoKey
 */
external interface RawCryptoKeyPair : CryptoKey {

    /**
     * `alias` the alias used to identify the key pair
     */
    val alias: String

    /**
     * `privateKey` the value of the stored encoded private key
     */
    val privateKey: String

    /**
     * `publicKey` the value of the stored encoded public key
     */
    val publicKey: String

    /**
     * `publicKeyUsages` indicates what can be done with the public key
     */
    val publicKeyUsages: JsArray<JsString>

}