package com.tecknobit.kassaforte.wrappers.crypto.key.raw

import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey

/**
 * The `RawCryptoKey` interface represents the data of a stored key
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see kotlin.js.JsAny
 * @see CryptoKey
 */
external interface RawCryptoKey : CryptoKey {

    /**
     * `alias` the alias used to identify the key
     */
    val alias: String

    /**
     * `key` the value of the stored encoded key
     */
    val key: String

}