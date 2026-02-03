package com.tecknobit.kassaforte.wrappers.crypto.params

import org.khronos.webgl.ArrayBuffer

/**
 * The `AesCtrParams` interface wraps the native [AesCtrParams](https://developer.mozilla.org/en-US/docs/Web/API/AesCtrParams)
 * object
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see JsAny
 * @see com.tecknobit.kassaforte.wrappers.crypto.params.EncryptionParams
 */
external interface AesCtrParams : EncryptionParams {

    /**
     * `counter` the initial value of the counter block. This must be 16 bytes long (the AES block size). The rightmost
     * length bits of this block are used for the counter, and the rest is used for the nonce.
     * For example, if [length] is set to `64`, then the first half of counter is the nonce and the second half is used for
     * the counter
     */
    val counter: ArrayBuffer

    /**
     * `length` the number of bits in the counter block that are used for the actual counter. The counter must be big
     * enough that it doesn't wrap: if the message is `n` blocks and the counter is `m` bits long,
     * then the following must be true: `n <= 2^m`
     */
    val length: Int

}