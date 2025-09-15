package com.tecknobit.kassaforte.wrappers.crypto.params

import org.khronos.webgl.ArrayBuffer

external interface AesCbcParams : AesParams {

    val iv: ArrayBuffer

}