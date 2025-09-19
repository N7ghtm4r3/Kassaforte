package com.tecknobit.kassaforte.wrappers.crypto.params

import org.khronos.webgl.ArrayBuffer

external interface AesGcmParams : EncryptionParams {

    val iv: ArrayBuffer

}