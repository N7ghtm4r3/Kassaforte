@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.wrappers.crypto.key.genspec

import com.tecknobit.equinoxcore.annotations.Structure

/**
 * The `KeyGenSpec` interface provides the basic information for the generation of a key
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see JsAny
 */
@Structure
external interface KeyGenSpec : JsAny {

    /**
     * `name` the name of the algorithm which the key will use
     */
    val name: String

}

