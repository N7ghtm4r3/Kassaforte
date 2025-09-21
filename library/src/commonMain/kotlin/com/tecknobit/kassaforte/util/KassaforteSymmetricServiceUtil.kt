package com.tecknobit.kassaforte.util

import com.tecknobit.kassaforte.key.genspec.BlockMode
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.NONE
import com.tecknobit.kassaforte.services.KassaforteSymmetricService

/**
 * Method used to decrypt encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param blockMode The block mode to use to encrypt data
 * @param padding The padding to apply to encrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [Int]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToInt(
    alias: String,
    blockMode: BlockMode,
    padding: EncryptionPadding = NONE,
    data: String,
): Int? {
    return decrypt(
        alias = alias,
        blockMode = blockMode,
        padding = padding,
        data = data
    ).toIntOrNull()
}

/**
 * Method used to decrypt encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param blockMode The block mode to use to encrypt data
 * @param padding The padding to apply to encrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [UInt]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToUInt(
    alias: String,
    blockMode: BlockMode,
    padding: EncryptionPadding = NONE,
    data: String,
): UInt? {
    return decrypt(
        alias = alias,
        blockMode = blockMode,
        padding = padding,
        data = data
    ).toUIntOrNull()
}

/**
 * Method used to decrypt encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param blockMode The block mode to use to encrypt data
 * @param padding The padding to apply to encrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [Long]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToLong(
    alias: String,
    blockMode: BlockMode,
    padding: EncryptionPadding = NONE,
    data: String,
): Long? {
    return decrypt(
        alias = alias,
        blockMode = blockMode,
        padding = padding,
        data = data
    ).toLongOrNull()
}

/**
 * Method used to decrypt encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param blockMode The block mode to use to encrypt data
 * @param padding The padding to apply to encrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [ULong]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToULong(
    alias: String,
    blockMode: BlockMode,
    padding: EncryptionPadding = NONE,
    data: String,
): ULong? {
    return decrypt(
        alias = alias,
        blockMode = blockMode,
        padding = padding,
        data = data
    ).toULongOrNull()
}

/**
 * Method used to decrypt encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param blockMode The block mode to use to encrypt data
 * @param padding The padding to apply to encrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [Short]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToShort(
    alias: String,
    blockMode: BlockMode,
    padding: EncryptionPadding = NONE,
    data: String,
): Short? {
    return decrypt(
        alias = alias,
        blockMode = blockMode,
        padding = padding,
        data = data
    ).toShortOrNull()
}

/**
 * Method used to decrypt encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param blockMode The block mode to use to encrypt data
 * @param padding The padding to apply to encrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [UShort]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToUShort(
    alias: String,
    blockMode: BlockMode,
    padding: EncryptionPadding = NONE,
    data: String,
): UShort? {
    return decrypt(
        alias = alias,
        blockMode = blockMode,
        padding = padding,
        data = data
    ).toUShortOrNull()
}

/**
 * Method used to decrypt encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param blockMode The block mode to use to encrypt data
 * @param padding The padding to apply to encrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [Byte]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToByte(
    alias: String,
    blockMode: BlockMode,
    padding: EncryptionPadding = NONE,
    data: String,
): Byte? {
    return decrypt(
        alias = alias,
        blockMode = blockMode,
        padding = padding,
        data = data
    ).toByteOrNull()
}

/**
 * Method used to decrypt encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param blockMode The block mode to use to encrypt data
 * @param padding The padding to apply to encrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [UByte]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToUByte(
    alias: String,
    blockMode: BlockMode,
    padding: EncryptionPadding = NONE,
    data: String,
): UByte? {
    return decrypt(
        alias = alias,
        blockMode = blockMode,
        padding = padding,
        data = data
    ).toUByteOrNull()
}

/**
 * Method used to decrypt encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param blockMode The block mode to use to encrypt data
 * @param padding The padding to apply to encrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [Float]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToFloat(
    alias: String,
    blockMode: BlockMode,
    padding: EncryptionPadding = NONE,
    data: String,
): Float? {
    return decrypt(
        alias = alias,
        blockMode = blockMode,
        padding = padding,
        data = data
    ).toFloatOrNull()
}

/**
 * Method used to decrypt encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param blockMode The block mode to use to encrypt data
 * @param padding The padding to apply to encrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [Double]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToDouble(
    alias: String,
    blockMode: BlockMode,
    padding: EncryptionPadding = NONE,
    data: String,
): Double? {
    return decrypt(
        alias = alias,
        blockMode = blockMode,
        padding = padding,
        data = data
    ).toDoubleOrNull()
}

/**
 * Method used to decrypt encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param blockMode The block mode to use to encrypt data
 * @param padding The padding to apply to encrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [Boolean]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToBoolean(
    alias: String,
    blockMode: BlockMode,
    padding: EncryptionPadding = NONE,
    data: String,
): Boolean? {
    return decrypt(
        alias = alias,
        blockMode = blockMode,
        padding = padding,
        data = data
    ).toBooleanStrictOrNull()
}

/**
 * Method used to decrypt encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param blockMode The block mode to use to encrypt data
 * @param padding The padding to apply to encrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [Char]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToChar(
    alias: String,
    blockMode: BlockMode,
    padding: EncryptionPadding = NONE,
    data: String,
): Char? {
    return decrypt(
        alias = alias,
        blockMode = blockMode,
        padding = padding,
        data = data
    ).singleOrNull()
}