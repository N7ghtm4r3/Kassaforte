@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.crypto.params

import com.tecknobit.equinoxcore.annotations.Structure
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny

/**
 * The `EncryptionParams` interface provides the basic information how to use the key to encrypt or decrypt the data
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see kotlin.js.JsAny
 */
@Structure
sealed external interface EncryptionParams : JsAny {

    /**
     * `name` the name of the algorithm to use
     */
    val name: String

}