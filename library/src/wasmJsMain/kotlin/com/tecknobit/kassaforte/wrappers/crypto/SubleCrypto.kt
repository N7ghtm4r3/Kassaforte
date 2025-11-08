@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.crypto

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.KeyGenSpec
import com.tecknobit.kassaforte.wrappers.crypto.params.*
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import kotlin.js.Promise

/**
 * The `SubtleCrypto` interface wraps the native [SubtleCrypto](https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto)
 * object
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see JsAny
 */
external interface SubtleCrypto : JsAny {

    /**
     * Method used to generate a new key (for symmetric algorithms) or key pair (for public-key algorithms)
     *
     * @param algorithm The type of key to generate and providing extra algorithm-specific parameters
     * @param extractable Whether it will be possible to export the key using
     * @param keyUsages What can be done with the newly generated key
     *
     * @return the generated key as [Promise] of [JsAny]
     */
    fun generateKey(
        algorithm: KeyGenSpec,
        extractable: Boolean,
        keyUsages: JsArray<JsString>,
    ): Promise<JsAny>

    /**
     * Method used to export a key: that is, it takes as input a [CryptoKey] object and gives you the key in an external,
     * portable format
     *
     * @param format Describes the data format in which the key should be exported
     * @param key The key to export
     *
     * @return the exported key as [Promise] of [ArrayBuffer]
     */
    fun exportKey(
        format: String,
        key: CryptoKey,
    ): Promise<ArrayBuffer>

    /**
     * Method used to import a key: that is, it takes as input a key in an external, portable format and gives you a
     * [CryptoKey]
     *
     * @param format Describes the data format of the key to import
     * @param keyData Contains the key in the given format
     * @param algorithm Defines the type of key to import and providing extra algorithm-specific parameters
     * @param extractable Whether it will be possible to export the key using
     * @param keyUsages Indicates what can be done with the key
     *
     * @return the imported key as [Promise] of [JsAny]
     */
    fun importKey(
        format: String,
        keyData: ArrayBuffer,
        algorithm: KeyGenSpec,
        extractable: Boolean,
        keyUsages: JsArray<JsString>,
    ): Promise<JsAny>

    /**
     * Method used to encrypt data
     *
     * @param algorithm Specifies the algorithm to be used and any extra parameters if required
     * @param key The key to be used for encryption
     * @param data The data to be encrypted
     *
     * @return the encrypted data as [Promise] of [ArrayBuffer]
     */
    fun encrypt(
        algorithm: JsAny,
        key: CryptoKey,
        data: Uint8Array,
    ): Promise<ArrayBuffer>

    /**
     * Method used to decrypt data
     *
     * @param algorithm Specifies the algorithm to be used and any extra parameters if required
     * @param key The key to be used for decryption
     * @param data The data to be decrypted
     *
     * @return the decrypted data as [Promise] of [ArrayBuffer]
     */
    fun decrypt(
        algorithm: JsAny,
        key: CryptoKey,
        data: Uint8Array,
    ): Promise<ArrayBuffer>

    /**
     * Method used to sign message
     *
     * @param algorithm The algorithm to use to sign the message
     * @param key The key to use to sign the message
     * @param data The data of the message to sign
     *
     * @return the signed message as [Promise] of [ArrayBuffer]
     *
     * @since Revision Two
     */
    fun sign(
        algorithm: JsAny,
        key: CryptoKey,
        data: Uint8Array,
    ): Promise<ArrayBuffer>

    /**
     * Method used to verify the validity of a message
     *
     * @param algorithm The algorithm to use to verify the message
     * @param key The key to use to verify the message
     * @param signature The signature previously computed
     * @param data The data of the message to verify
     *
     * @return whether the message matches to [signature] as [Promise] of [JsBoolean]
     *
     * @since Revision Two
     */
    fun verify(
        algorithm: JsAny,
        key: CryptoKey,
        signature: Uint8Array,
        data: Uint8Array,
    ): Promise<JsBoolean>

}

/**
 * Method used to obtain an instance of [SubtleCrypto]
 *
 * @return a subtle crypto instance as [SubtleCrypto]
 */
@Returner
@JsFun("() => window.crypto.subtle")
external fun subtleCrypto(): SubtleCrypto

/**
 * Method used to assemble the parameters to use with `AES-CBC` algorithm
 *
 * @param name The name of the algorithm to use
 * @param iv The initialization vector. Must be 16 bytes, unpredictable, and preferably cryptographically random.
 * However, it need not be secret (for example, it may be transmitted unencrypted along with the ciphertext)
 *
 * @return the `CBC` params as [AesCbcParams]
 */
@JsFun(
    """
    (name, iv) => (
       {
          name: name,
          iv: (() => {
             if(iv.byteLength === 0) {
                const array = new Uint8Array(16);
                crypto.getRandomValues(array);
                return array.buffer;
             } else 
                return iv;
          })()
       }
    )   
    """
)
@Assembler
external fun aesCbcParams(
    name: String,
    iv: ArrayBuffer,
): AesCbcParams

/**
 * Method used to assemble the parameters to use with `AES-CTR` algorithm
 *
 * @param name The name of the algorithm to use
 * @param counter The initial value of the counter block. This must be 16 bytes long (the AES block size). The rightmost
 * length bits of this block are used for the counter, and the rest is used for the nonce.
 * For example, if [length] is set to `64`, then the first half of counter is the nonce and the second half is used for
 * the counter
 *
 * @return the `CTR` params as [AesCtrParams]
 */
@JsFun(
    """
    (name, counter) => (
       {
          name: name,
          counter: (() => {
             if(counter.byteLength === 0) {
                const array = new Uint8Array(16);
                crypto.getRandomValues(array);
                return array.buffer;
             } else 
                return counter;
          })(),
          length: 64
       }
    )   
    """
)
@Assembler
external fun aesCtrParams(
    name: String,
    counter: ArrayBuffer,
): AesCtrParams

/**
 * Method used to assemble the parameters to use with `AES-GCM` algorithm
 *
 * @param name The name of the algorithm to use
 * @param iv This must be unique for every encryption operation carried out with a given key. Put another way: never
 * reuse an `IV` with the same key. The `AES-GCM` specification recommends that the `IV` should be `96` bits long, and
 * typically contains bits from a random number generator
 *
 * @return the `GCM` params as [AesGcmParams]
 */
@JsFun(
    """
    (name, iv) => (
       {
          name: name,
          iv: (() => {
             if(iv.byteLength === 0) {
                const array = new Uint8Array(12);
                crypto.getRandomValues(array);
                return array.buffer;
             } else 
                return iv;
          })()
       }
    )   
    """
)
@Assembler
external fun aesGcmParams(
    name: String,
    iv: ArrayBuffer,
): AesGcmParams

/**
 * Method used to assemble the parameters to use with `RSA-OAEP` algorithm
 *
 * @return the `RSA-OAEP` params as [EncryptionParams]
 */
@JsFun(
    """
    (name) => (
       {
          name: "RSA-OAEP"
       }
    )   
    """
)
@Assembler
external fun rsaOaepParams(): EncryptionParams

/**
 * Method used to assemble the parameters to use with `HMAC` algorithm and associated hash function
 *
 * @return the `HMAC` params as [HmacParams]
 */
@JsFun(
    """
    (name, hash) => (
       {
          name: "HMAC",
          hash: hash
       }
    )   
    """
)
@Assembler
external fun hmacParams(
    hash: String,
): HmacParams