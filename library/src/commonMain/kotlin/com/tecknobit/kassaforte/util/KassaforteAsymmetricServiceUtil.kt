package com.tecknobit.kassaforte.util

import com.tecknobit.kassaforte.key.genspec.AlgorithmType.RSA
import com.tecknobit.kassaforte.key.genspec.DigestType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.services.KassaforteAsymmetricService

/**
 * `UNSUPPORTED_CIPHER_ALGORITHM` the error message to display when the algorithm is not supported
 */
const val UNSUPPORTED_CIPHER_ALGORITHM = "The %s algorithm is not supported to cipher"

/**
 * Validator method used to check whether the [algorithm] is supported to be used in an asymmetric ciphering
 *
 * @param algorithm The algorithm to check
 */
// TODO: ANNOTATE WITH @Validator
internal fun checkIfIsSupportedCipherAlgorithm(
    algorithm: String,
) {
    require(algorithm == RSA.value) { UNSUPPORTED_CIPHER_ALGORITHM.replace("%s", algorithm) }
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToInt(
    alias: String,
    paddingType: EncryptionPaddingType? = null,
    digestType: DigestType? = null,
    data: String,
): Int? {
    return decrypt(
        alias = alias,
        paddingType = paddingType,
        digestType = digestType,
        data = data
    ).toIntOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToUInt(
    alias: String,
    paddingType: EncryptionPaddingType? = null,
    digestType: DigestType? = null,
    data: String,
): UInt? {
    return decrypt(
        alias = alias,
        paddingType = paddingType,
        digestType = digestType,
        data = data
    ).toUIntOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToLong(
    alias: String,
    paddingType: EncryptionPaddingType? = null,
    digestType: DigestType? = null,
    data: String,
): Long? {
    return decrypt(
        alias = alias,
        paddingType = paddingType,
        digestType = digestType,
        data = data
    ).toLongOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToULong(
    alias: String,
    paddingType: EncryptionPaddingType? = null,
    digestType: DigestType? = null,
    data: String,
): ULong? {
    return decrypt(
        alias = alias,
        paddingType = paddingType,
        digestType = digestType,
        data = data
    ).toULongOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToShort(
    alias: String,
    paddingType: EncryptionPaddingType? = null,
    digestType: DigestType? = null,
    data: String,
): Short? {
    return decrypt(
        alias = alias,
        paddingType = paddingType,
        digestType = digestType,
        data = data
    ).toShortOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToUShort(
    alias: String,
    paddingType: EncryptionPaddingType? = null,
    digestType: DigestType? = null,
    data: String,
): UShort? {
    return decrypt(
        alias = alias,
        paddingType = paddingType,
        digestType = digestType,
        data = data
    ).toUShortOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToByte(
    alias: String,
    paddingType: EncryptionPaddingType? = null,
    digestType: DigestType? = null,
    data: String,
): Byte? {
    return decrypt(
        alias = alias,
        paddingType = paddingType,
        digestType = digestType,
        data = data
    ).toByteOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToUByte(
    alias: String,
    paddingType: EncryptionPaddingType? = null,
    digestType: DigestType? = null,
    data: String,
): UByte? {
    return decrypt(
        alias = alias,
        paddingType = paddingType,
        digestType = digestType,
        data = data
    ).toUByteOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToFloat(
    alias: String,
    paddingType: EncryptionPaddingType? = null,
    digestType: DigestType? = null,
    data: String,
): Float? {
    return decrypt(
        alias = alias,
        paddingType = paddingType,
        digestType = digestType,
        data = data
    ).toFloatOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToDouble(
    alias: String,
    paddingType: EncryptionPaddingType? = null,
    digestType: DigestType? = null,
    data: String,
): Double? {
    return decrypt(
        alias = alias,
        paddingType = paddingType,
        digestType = digestType,
        data = data
    ).toDoubleOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToBoolean(
    alias: String,
    paddingType: EncryptionPaddingType? = null,
    digestType: DigestType? = null,
    data: String,
): Boolean? {
    return decrypt(
        alias = alias,
        paddingType = paddingType,
        digestType = digestType,
        data = data
    ).toBooleanStrictOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToChar(
    alias: String,
    paddingType: EncryptionPaddingType? = null,
    digestType: DigestType? = null,
    data: String,
): Char? {
    return decrypt(
        alias = alias,
        paddingType = paddingType,
        digestType = digestType,
        data = data
    ).singleOrNull()
}