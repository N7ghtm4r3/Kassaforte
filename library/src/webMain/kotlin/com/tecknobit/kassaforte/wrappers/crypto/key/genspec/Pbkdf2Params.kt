package com.tecknobit.kassaforte.wrappers.crypto.key.genspec

import org.khronos.webgl.ArrayBuffer

/**
 * The `Pbkdf2Params` interface wraps the native [Pbkdf2Params](https://developer.mozilla.org/en-US/docs/Web/API/Pbkdf2Params)
 * object
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see kotlin.js.JsAny
 * @see com.tecknobit.kassaforte.wrappers.crypto.key.genspec.KeyGenSpec
 *
 * @since Revision Three
 */
external interface Pbkdf2Params : KeyGenSpec {

    /**
     * `hash` the digest algorithm to use
     */
    val hash: String

    /**
     * `salt` random or pseudo-random value of at least 16 bytes, does not need to be kept secret
     */
    val salt: ArrayBuffer

    /**
     * `iterations` representing the number of times the hash function will be executed during derivation
     */
    val iterations: Int

}