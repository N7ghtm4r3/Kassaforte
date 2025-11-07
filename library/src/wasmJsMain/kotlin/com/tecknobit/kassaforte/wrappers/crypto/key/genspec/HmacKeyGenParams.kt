package com.tecknobit.kassaforte.wrappers.crypto.key.genspec

/**
 * The `HmacKeyGenParams` interface represents the information of a generated key to perform `HMAC` operations
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see JsAny
 * @see KeyGenSpec
 * @see SymmetricKeyGenSpec
 *
 * @since Revision Two
 */
external interface HmacKeyGenParams : SymmetricKeyGenSpec {

    /**
     * `hash` the hash function associated with the key
     */
    val hash: String

}