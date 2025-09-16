@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.crypto

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey
import com.tecknobit.kassaforte.wrappers.crypto.key.KeyGenSpec
import com.tecknobit.kassaforte.wrappers.crypto.params.AesCbcParams
import com.tecknobit.kassaforte.wrappers.crypto.params.AesCtrParams
import com.tecknobit.kassaforte.wrappers.crypto.params.AesGcmParams
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import kotlin.js.Promise

const val RAW_EXPORT_FORMAT: String = "raw"

external interface SubtleCrypto {

    fun generateKey(
        algorithm: KeyGenSpec,
        extractable: Boolean,
        keyUsages: JsArray<JsString>,
    ): Promise<JsAny>

    fun exportKey(
        format: String,
        key: CryptoKey,
    ): Promise<ArrayBuffer>

    fun importKey(
        format: String,
        keyData: ArrayBuffer,
        algorithm: KeyGenSpec,
        extractable: Boolean,
        keyUsages: JsArray<JsString>,
    ): Promise<JsAny>

    fun encrypt(
        algorithm: JsAny,
        key: CryptoKey,
        data: Uint8Array,
    ): Promise<ArrayBuffer>

    fun decrypt(
        algorithm: JsAny,
        key: CryptoKey,
        data: Uint8Array,
    ): Promise<ArrayBuffer>

}

@JsFun("() => window.crypto.subtle")
external fun subtleCrypto(): SubtleCrypto

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