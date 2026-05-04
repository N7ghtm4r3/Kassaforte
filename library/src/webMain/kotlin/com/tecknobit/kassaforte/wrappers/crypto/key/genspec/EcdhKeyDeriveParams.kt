package com.tecknobit.kassaforte.wrappers.crypto.key.genspec

import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey

/**
 * The `EcdhKeyDeriveParams` interface wraps the native [EcdhKeyDeriveParams](https://developer.mozilla.org/en-US/docs/Web/API/EcdhKeyDeriveParams)
 * object
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see kotlin.js.JsAny
 * @see com.tecknobit.kassaforte.wrappers.crypto.key.genspec.KeyGenSpec
 *
 * @since Revision Threee
 */
external interface EcdhKeyDeriveParams : KeyGenSpec {

    /**
     * `public` The object representing the public key of the other entity
     */
    val public: CryptoKey

}