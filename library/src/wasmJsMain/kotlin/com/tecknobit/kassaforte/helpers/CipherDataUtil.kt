package com.tecknobit.kassaforte.helpers

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.BlockModeType
import com.tecknobit.kassaforte.key.genspec.BlockModeType.CBC
import com.tecknobit.kassaforte.key.genspec.BlockModeType.GCM
import com.tecknobit.kassaforte.services.CBC_CTR_BLOCK_SIZE
import com.tecknobit.kassaforte.services.GCM_BLOCK_SIZE
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array

fun Any.prepareToEncrypt(
    blockModeType: BlockModeType,
): Uint8Array {
    val plainText = this.toString().encodeToByteArray()
    return if (blockModeType == CBC) {
        plainText.pad(
            blockModeType = blockModeType
        )
    } else
        plainText.toUint8Array()
}

private fun ByteArray.pad(
    blockModeType: BlockModeType,
): Uint8Array {
    val blockSize = computeBlockSize(
        blockModeType = blockModeType
    )
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
    blockModeType: BlockModeType,
): String {
    val unpaddedData = this.toByteArray()
    val plainTextBytes = if (blockModeType == CBC) {
        unpaddedData.unPad(
            blockModeType = blockModeType
        )
    } else
        unpaddedData
    return plainTextBytes.decodeToString()
}

fun ByteArray.unPad(
    blockModeType: BlockModeType,
): ByteArray {
    val blockSize = computeBlockSize(
        blockModeType = blockModeType
    )
    val padding = this.last().toInt()
    if (padding !in 1..blockSize)
        throw IllegalArgumentException("Invalid padding")
    return this.copyOfRange(0, this.size - padding)
}

@Returner
private fun computeBlockSize(
    blockModeType: BlockModeType,
): Int {
    return when (blockModeType) {
        GCM -> GCM_BLOCK_SIZE
        else -> CBC_CTR_BLOCK_SIZE
    }
}