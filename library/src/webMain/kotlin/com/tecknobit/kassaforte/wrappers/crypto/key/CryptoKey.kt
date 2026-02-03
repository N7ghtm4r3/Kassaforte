@file:OptIn(ExperimentalWasmJsInterop::class, ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.crypto.key

import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.KeyGenSpec
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsArray
import kotlin.js.JsString

/**
 * The `CryptoKey` interface wraps the native [CryptoKey](https://developer.mozilla.org/en-US/docs/Web/API/CryptoKey)
 * object
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see JsAny
 */
external interface CryptoKey : JsAny {

    /**
     * `extractable` indicates whether the key may be extracted
     */
    val extractable: Boolean

    /**
     * `algorithm` describes the algorithm for which this key can be used and any associated extra parameters
     */
    val algorithm: KeyGenSpec

    /**
     * `usages` indicates what can be done with the key
     */
    val usages: JsArray<JsString>

}