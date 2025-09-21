package com.tecknobit.kassaforte.helpers

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.BlockMode
import com.tecknobit.kassaforte.key.genspec.BlockMode.CBC
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array

fun Any.prepareToEncrypt(
    blockMode: BlockMode? = null,
): Uint8Array {
    val plainText = this.toString().encodeToByteArray()
    return if (blockMode == CBC) {
        plainText.pad(
            blockMode = blockMode
        )
    } else
        plainText.toUint8Array()
}

private fun ByteArray.pad(
    blockMode: BlockMode,
): Uint8Array {
    val blockSize = blockMode.blockSize
    val padding = blockSize - (this.size % blockSize)
    val paddedArray = this + ByteArray(padding) { padding.toByte() }
    return paddedArray.toUint8Array()
}

@Returner
fun Any.prepareToDecrypt(): Uint8Array {
    return if (this is ByteArray)
        this.toUint8Array()
    else
        toString().encodeToByteArray().toUint8Array()
}

fun ArrayBuffer.asPlainText(
    blockMode: BlockMode? = null,
): String {
    val unpaddedData = this.toByteArray()
    val plainTextBytes = if (blockMode == CBC) {
        unpaddedData.unPad(
            blockMode = blockMode
        )
    } else
        unpaddedData
    return plainTextBytes.decodeToString()
}

fun ByteArray.unPad(
    blockMode: BlockMode,
): ByteArray {
    val padding = this.last().toInt()
    if (padding !in 1..blockMode.blockSize)
        throw IllegalArgumentException("Invalid padding")
    return this.copyOfRange(0, this.size - padding)
}