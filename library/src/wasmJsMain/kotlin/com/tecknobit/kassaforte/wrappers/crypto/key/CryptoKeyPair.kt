@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.crypto.key

/**
 * The `CryptoKeyPair` interface wraps the native [CryptoKeyPair](https://developer.mozilla.org/en-US/docs/Web/API/CryptoKeyPair)
 * object
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see JsAny
 */
external interface CryptoKeyPair : JsAny {

    /**
     * `privateKey` represents the private key. For encryption and decryption algorithms, this key is used to decrypt.
     * For signing and verification algorithms it is used to sign
     */
    val privateKey: CryptoKey

    /**
     * `publicKey` represents the public key. For encryption and decryption algorithms, this key is used to encrypt.
     * For signing and verification algorithms it is used to verify signatures.
     */
    val publicKey: CryptoKey

}