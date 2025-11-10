package com.tecknobit.kassaforte.helpers

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.BlockMode
import com.tecknobit.kassaforte.key.genspec.BlockMode.CBC
import com.tecknobit.kassaforte.util.encodeForKeyOperation
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array

/**
 * Method used to prepare the data to be encrypted
 *
 * @param blockMode The block mode to use to prepare the data
 *
 * @return the data prepared to be encrypted as [Uint8Array]
 */
fun Any.prepareToEncrypt(
    blockMode: BlockMode? = null,
): Uint8Array {
    val plainText = encodeForKeyOperation()
    return if (blockMode == CBC) {
        plainText.pad(
            blockMode = blockMode
        )
    } else
        plainText.toUint8Array()
}

/**
 * Method used to apply padding to the data to encrypt
 *
 * @param blockMode The block mode to use to pad the data
 *
 * @return the padded data as [Uint8Array]
 */
private fun ByteArray.pad(
    blockMode: BlockMode,
): Uint8Array {
    val blockSize = blockMode.blockSize
    val padding = blockSize - (this.size % blockSize)
    val paddedArray = this + ByteArray(padding) { padding.toByte() }
    return paddedArray.toUint8Array()
}

/**
 * Method used to convert an [ArrayBuffer] into the decrypted plaintext
 *
 * @param blockMode The block mode used to pad the data before encrypting it
 *
 * @return the plaintext as [String]
 */
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

/**
 * Method used to remove padding applied to the data before the encryption
 *
 * @param blockMode The block mode used to pad the data
 *
 * @return the unpadded data as [ByteArray]
 */
fun ByteArray.unPad(
    blockMode: BlockMode,
): ByteArray {
    val padding = this.last().toInt()
    if (padding !in 1..blockMode.blockSize)
        throw IllegalArgumentException("Invalid padding")
    return this.copyOfRange(0, this.size - padding)
}

/**
 * Method used to prepare the data to be decrypted
 *
 * @return the data prepared to be decrypted as [Uint8Array]
 */
@Returner
fun Any.toUint8Array(): Uint8Array {
    return if (this is ByteArray)
        this.toUint8Array()
    else
        encodeForKeyOperation().toUint8Array()
}

