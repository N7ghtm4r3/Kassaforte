@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.helpers

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.equinoxcore.annotations.Wrapper
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

/**
 * Method used to convert an [ArrayBuffer] into a [ByteArray]
 *
 * @return the array buffer converted as [ByteArray]
 */
@Returner
fun ArrayBuffer.toByteArray(): ByteArray {
    val uInt8 = Uint8Array(this)
    return ByteArray(uInt8.length) { index -> uInt8[index] }
}

/**
 * Method used to convert an [ByteArray] into a [ArrayBuffer]
 *
 * @return the byte array converted as [ArrayBuffer]
 */
@Wrapper
@Returner
fun ByteArray.toArrayBuffer(): ArrayBuffer {
    return this.toUint8Array().buffer
}

/**
 * Method used to convert an [ByteArray] into a [Uint8Array]
 *
 * @return the byte array converted as [Uint8Array]
 */
@Returner
fun ByteArray.toUint8Array(): Uint8Array {
    val uint8Array = Uint8Array(this.size)
    val array = this.map { it.toInt().toJsNumber() }.toJsArray()
    uint8Array.set(
        array = array
    )
    return uint8Array
}