@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.crypto

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey
import com.tecknobit.kassaforte.wrappers.crypto.key.KeyGenSpec
import com.tecknobit.kassaforte.wrappers.crypto.params.AesCbcParams
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
    ): Promise<JsAny>

}

@Returner
fun ByteArray.toUint8Array(): Uint8Array {
    val uint8Array = Uint8Array(this.size)
    val array = this.map { it.toInt().toJsNumber() }.toJsArray()
    uint8Array.set(
        array = array
    )
    return uint8Array
}

@JsFun("() => window.crypto.subtle")
external fun subtleCrypto(): SubtleCrypto

@JsFun(
    """
    (name) => (
       {
          name: name,
          iv: (() => {
             const array = new Uint8Array(16);
             crypto.getRandomValues(array);
             return array.buffer;
          })()
       }
    )   
    """
)
@Assembler
external fun aesCbcParams(
    name: String,
): AesCbcParams

@JsFun(
    """
    (name) => (
       {
          name: name,
          iv: (() => {
             const array = new Uint8Array(16);
             crypto.getRandomValues(array);
             return array.buffer;
          })()
       }
    )   
    """
)
@Assembler
external fun aesCtrParams(
    name: String,
): JsAny

@JsFun(
    """
    (name) => (
       {
          name: name,
          iv: (() => {
             const array = new Uint8Array(16);
             crypto.getRandomValues(array);
             return array.buffer;
          })()
       }
    )   
    """
)
@Assembler
external fun aesGmcParams(
    name: String,
): JsAny