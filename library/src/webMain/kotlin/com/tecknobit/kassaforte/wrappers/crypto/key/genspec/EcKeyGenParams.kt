package com.tecknobit.kassaforte.wrappers.crypto.key.genspec

/**
 * The `EcKeyGenParams` interface wraps the native [EcKeyGenParams](https://developer.mozilla.org/en-US/docs/Web/API/EcKeyGenParams)
 * object
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see JsAny
 * @see com.tecknobit.kassaforte.wrappers.crypto.key.genspec.KeyGenSpec
 */
external interface EcKeyGenParams : KeyGenSpec {

    /**
     * `namedCurve` represents the name of the elliptic curve to use
     */
    val namedCurve: String

}