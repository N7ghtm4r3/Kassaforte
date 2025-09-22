package com.tecknobit.kassaforte.wrappers.crypto.params

import org.khronos.webgl.ArrayBuffer

/**
 * The `AesGcmParams` interface wraps the native [AesGcmParams](https://developer.mozilla.org/en-US/docs/Web/API/AesGcmParams)
 * object
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see JsAny
 * @see EncryptionParams
 */
external interface AesGcmParams : EncryptionParams {

    /**
     * `iv` This must be unique for every encryption operation carried out with a given key. Put another way: never
     * reuse an `IV` with the same key. The `AES-GCM` specification recommends that the `IV` should be `96` bits long, and typically
     * contains bits from a random number generator
     */
    val iv: ArrayBuffer

}