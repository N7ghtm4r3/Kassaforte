package com.tecknobit.kassaforte.wrappers.crypto.key.genspec

/**
 * The `SymmetricKeyGenSpec` interface represents the information of a generated symmetric key
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see JsAny
 * @see KeyGenSpec
 */
external interface SymmetricKeyGenSpec : KeyGenSpec {

    /**
     * `length` the length in bits of the symmetric key
     */
    val length: Int

}

