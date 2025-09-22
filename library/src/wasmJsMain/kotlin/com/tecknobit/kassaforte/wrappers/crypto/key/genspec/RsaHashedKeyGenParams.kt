package com.tecknobit.kassaforte.wrappers.crypto.key.genspec

import org.khronos.webgl.Uint8Array

/**
 * The `RsaHashedKeyGenParams` interface wraps the native [RsaHashedKeyGenParams](https://developer.mozilla.org/en-US/docs/Web/API/RsaHashedKeyGenParams)
 * object
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see JsAny
 * @see KeyGenSpec
 */
external interface RsaHashedKeyGenParams : KeyGenSpec {

    /**
     * `modulusLength` the length in bits of the RSA modulus
     */
    val modulusLength: Int

    /**
     * `publicExponent` the public exponent
     */
    val publicExponent: Uint8Array

    /**
     * `hash` is the identifier for the digest algorithm to use
     */
    val hash: String

}

