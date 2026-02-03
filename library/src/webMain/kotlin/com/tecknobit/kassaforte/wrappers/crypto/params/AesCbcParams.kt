package com.tecknobit.kassaforte.wrappers.crypto.params

import org.khronos.webgl.ArrayBuffer

/**
 * The `AesCbcParams` interface wraps the native [AesCbcParams](https://developer.mozilla.org/en-US/docs/Web/API/AesCbcParams)
 * object
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see kotlin.js.JsAny
 * @see com.tecknobit.kassaforte.wrappers.crypto.params.EncryptionParams
 */
external interface AesCbcParams : EncryptionParams {

    /**
     * `iv` the initialization vector. Must be 16 bytes, unpredictable, and preferably cryptographically random.
     * However, it need not be secret (for example, it may be transmitted unencrypted along with the ciphertext)
     */
    val iv: ArrayBuffer

}