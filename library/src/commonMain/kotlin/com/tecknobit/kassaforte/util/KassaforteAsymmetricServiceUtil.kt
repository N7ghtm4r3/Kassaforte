package com.tecknobit.kassaforte.util

import com.tecknobit.kassaforte.key.genspec.Algorithm.RSA
import com.tecknobit.kassaforte.key.genspec.Digest
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
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

/**
 * Method used to decrypt the encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param padding The padding to apply to decrypt data
 * @param digest The digest to apply to decrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [Int]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToInt(
    alias: String,
    padding: EncryptionPadding? = null,
    digest: Digest? = null,
    data: String,
): Int? {
    return decrypt(
        alias = alias,
        padding = padding,
        digest = digest,
        data = data
    ).toIntOrNull()
}

/**
 * Method used to decrypt the encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param padding The padding to apply to decrypt data
 * @param digest The digest to apply to decrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [UInt]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToUInt(
    alias: String,
    padding: EncryptionPadding? = null,
    digest: Digest? = null,
    data: String,
): UInt? {
    return decrypt(
        alias = alias,
        padding = padding,
        digest = digest,
        data = data
    ).toUIntOrNull()
}

/**
 * Method used to decrypt the encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param padding The padding to apply to decrypt data
 * @param digest The digest to apply to decrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [Long]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToLong(
    alias: String,
    padding: EncryptionPadding? = null,
    digest: Digest? = null,
    data: String,
): Long? {
    return decrypt(
        alias = alias,
        padding = padding,
        digest = digest,
        data = data
    ).toLongOrNull()
}

/**
 * Method used to decrypt the encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param padding The padding to apply to decrypt data
 * @param digest The digest to apply to decrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [ULong]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToULong(
    alias: String,
    padding: EncryptionPadding? = null,
    digest: Digest? = null,
    data: String,
): ULong? {
    return decrypt(
        alias = alias,
        padding = padding,
        digest = digest,
        data = data
    ).toULongOrNull()
}

/**
 * Method used to decrypt the encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param padding The padding to apply to decrypt data
 * @param digest The digest to apply to decrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [Short]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToShort(
    alias: String,
    padding: EncryptionPadding? = null,
    digest: Digest? = null,
    data: String,
): Short? {
    return decrypt(
        alias = alias,
        padding = padding,
        digest = digest,
        data = data
    ).toShortOrNull()
}

/**
 * Method used to decrypt the encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param padding The padding to apply to decrypt data
 * @param digest The digest to apply to decrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [UShort]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToUShort(
    alias: String,
    padding: EncryptionPadding? = null,
    digest: Digest? = null,
    data: String,
): UShort? {
    return decrypt(
        alias = alias,
        padding = padding,
        digest = digest,
        data = data
    ).toUShortOrNull()
}

/**
 * Method used to decrypt the encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param padding The padding to apply to decrypt data
 * @param digest The digest to apply to decrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [Byte]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToByte(
    alias: String,
    padding: EncryptionPadding? = null,
    digest: Digest? = null,
    data: String,
): Byte? {
    return decrypt(
        alias = alias,
        padding = padding,
        digest = digest,
        data = data
    ).toByteOrNull()
}

/**
 * Method used to decrypt the encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param padding The padding to apply to decrypt data
 * @param digest The digest to apply to decrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [UByte]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToUByte(
    alias: String,
    padding: EncryptionPadding? = null,
    digest: Digest? = null,
    data: String,
): UByte? {
    return decrypt(
        alias = alias,
        padding = padding,
        digest = digest,
        data = data
    ).toUByteOrNull()
}

/**
 * Method used to decrypt the encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param padding The padding to apply to decrypt data
 * @param digest The digest to apply to decrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [Float]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToFloat(
    alias: String,
    padding: EncryptionPadding? = null,
    digest: Digest? = null,
    data: String,
): Float? {
    return decrypt(
        alias = alias,
        padding = padding,
        digest = digest,
        data = data
    ).toFloatOrNull()
}

/**
 * Method used to decrypt the encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param padding The padding to apply to decrypt data
 * @param digest The digest to apply to decrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [Double]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToDouble(
    alias: String,
    padding: EncryptionPadding? = null,
    digest: Digest? = null,
    data: String,
): Double? {
    return decrypt(
        alias = alias,
        padding = padding,
        digest = digest,
        data = data
    ).toDoubleOrNull()
}

/**
 * Method used to decrypt the encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param padding The padding to apply to decrypt data
 * @param digest The digest to apply to decrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [Boolean]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToBoolean(
    alias: String,
    padding: EncryptionPadding? = null,
    digest: Digest? = null,
    data: String,
): Boolean? {
    return decrypt(
        alias = alias,
        padding = padding,
        digest = digest,
        data = data
    ).toBooleanStrictOrNull()
}

/**
 * Method used to decrypt the encrypted data with the key specified by the [alias] value
 *
 * @param alias The alias which identify the key to use
 * @param padding The padding to apply to decrypt data
 * @param digest The digest to apply to decrypt data
 * @param data The data to decrypt
 *
 * @return the decrypted data as nullable [Char]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun KassaforteAsymmetricService.decryptToChar(
    alias: String,
    padding: EncryptionPadding? = null,
    digest: Digest? = null,
    data: String,
): Char? {
    return decrypt(
        alias = alias,
        padding = padding,
        digest = digest,
        data = data
    ).singleOrNull()
}