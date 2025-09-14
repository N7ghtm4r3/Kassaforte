@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers

import com.tecknobit.kassaforte.wrappers.cryptokey.KeyGenSpec
import kotlin.js.Promise

internal external object SubtleCrypto {

    fun generateKey(
        algorithm: KeyGenSpec,
        extractable: Boolean,
        keyUsages: JsArray<JsString>,
    ): Promise<JsAny>

}

@JsFun("() => window.crypto.subtle")
internal external fun subtleCrypto(): SubtleCrypto
