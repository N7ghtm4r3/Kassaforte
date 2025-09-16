package com.tecknobit.kassaforte.util

import com.tecknobit.kassaforte.key.genspec.BlockModeType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType.NONE
import com.tecknobit.kassaforte.services.KassaforteSymmetricService

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToInt(
    alias: String,
    blockModeType: BlockModeType,
    paddingType: EncryptionPaddingType = NONE,
    data: String,
): Int? {
    return decrypt(
        alias = alias,
        blockModeType = blockModeType,
        paddingType = paddingType,
        data = data
    ).toIntOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToUInt(
    alias: String,
    blockModeType: BlockModeType,
    paddingType: EncryptionPaddingType = NONE,
    data: String,
): UInt? {
    return decrypt(
        alias = alias,
        blockModeType = blockModeType,
        paddingType = paddingType,
        data = data
    ).toUIntOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToLong(
    alias: String,
    blockModeType: BlockModeType,
    paddingType: EncryptionPaddingType = NONE,
    data: String,
): Long? {
    return decrypt(
        alias = alias,
        blockModeType = blockModeType,
        paddingType = paddingType,
        data = data
    ).toLongOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToULong(
    alias: String,
    blockModeType: BlockModeType,
    paddingType: EncryptionPaddingType = NONE,
    data: String,
): ULong? {
    return decrypt(
        alias = alias,
        blockModeType = blockModeType,
        paddingType = paddingType,
        data = data
    ).toULongOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToShort(
    alias: String,
    blockModeType: BlockModeType,
    paddingType: EncryptionPaddingType = NONE,
    data: String,
): Short? {
    return decrypt(
        alias = alias,
        blockModeType = blockModeType,
        paddingType = paddingType,
        data = data
    ).toShortOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToUShort(
    alias: String,
    blockModeType: BlockModeType,
    paddingType: EncryptionPaddingType = NONE,
    data: String,
): UShort? {
    return decrypt(
        alias = alias,
        blockModeType = blockModeType,
        paddingType = paddingType,
        data = data
    ).toUShortOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToByte(
    alias: String,
    blockModeType: BlockModeType,
    paddingType: EncryptionPaddingType = NONE,
    data: String,
): Byte? {
    return decrypt(
        alias = alias,
        blockModeType = blockModeType,
        paddingType = paddingType,
        data = data
    ).toByteOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToUByte(
    alias: String,
    blockModeType: BlockModeType,
    paddingType: EncryptionPaddingType = NONE,
    data: String,
): UByte? {
    return decrypt(
        alias = alias,
        blockModeType = blockModeType,
        paddingType = paddingType,
        data = data
    ).toUByteOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToFloat(
    alias: String,
    blockModeType: BlockModeType,
    paddingType: EncryptionPaddingType = NONE,
    data: String,
): Float? {
    return decrypt(
        alias = alias,
        blockModeType = blockModeType,
        paddingType = paddingType,
        data = data
    ).toFloatOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToDouble(
    alias: String,
    blockModeType: BlockModeType,
    paddingType: EncryptionPaddingType = NONE,
    data: String,
): Double? {
    return decrypt(
        alias = alias,
        blockModeType = blockModeType,
        paddingType = paddingType,
        data = data
    ).toDoubleOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteSymmetricService.decryptToBoolean(
    alias: String,
    blockModeType: BlockModeType,
    paddingType: EncryptionPaddingType = NONE,
    data: String,
): Boolean? {
    return decrypt(
        alias = alias,
        blockModeType = blockModeType,
        paddingType = paddingType,
        data = data
    ).toBooleanStrictOrNull()
}