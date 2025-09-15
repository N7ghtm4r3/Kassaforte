@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers

import com.tecknobit.kassaforte.wrappers.cryptokey.CryptoKey
import com.tecknobit.kassaforte.wrappers.cryptokey.KeyGenSpec
import org.khronos.webgl.ArrayBuffer
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
        algorithm: KeyGenSpec,
        key: CryptoKey,
        data: ArrayBuffer,
    ): Promise<JsAny>

}

@JsFun("() => window.crypto.subtle")
external fun subtleCrypto(): SubtleCrypto
