package com.tecknobit.kassaforte.util

import com.tecknobit.kassaforte.Kassaforte

/**
 * Method used to withdraw safeguarded data
 *
 * @param key The key of the safeguarded data to withdraw
 *
 * @return the safeguarded data specified by the [key] as nullable [Int]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawInt(
    key: String,
) : Int? {
    return withdraw(
        key = key
    )?.toIntOrNull()
}

/**
 * Method used to withdraw safeguarded data
 *
 * @param key The key of the safeguarded data to withdraw
 *
 * @return the safeguarded data specified by the [key] as nullable [UInt]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawUInt(
    key: String,
) : UInt? {
    return withdraw(
        key = key
    )?.toUIntOrNull()
}

/**
 * Method used to withdraw safeguarded data
 *
 * @param key The key of the safeguarded data to withdraw
 *
 * @return the safeguarded data specified by the [key] as nullable [Long]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawLong(
    key: String,
) : Long? {
    return withdraw(
        key = key
    )?.toLongOrNull()
}

/**
 * Method used to withdraw safeguarded data
 *
 * @param key The key of the safeguarded data to withdraw
 *
 * @return the safeguarded data specified by the [key] as nullable [ULong]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawULong(
    key: String,
) : ULong? {
    return withdraw(
        key = key
    )?.toULongOrNull()
}

/**
 * Method used to withdraw safeguarded data
 *
 * @param key The key of the safeguarded data to withdraw
 *
 * @return the safeguarded data specified by the [key] as nullable [Short]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawShort(
    key: String,
) : Short? {
    return withdraw(
        key = key
    )?.toShortOrNull()
}

/**
 * Method used to withdraw safeguarded data
 *
 * @param key The key of the safeguarded data to withdraw
 *
 * @return the safeguarded data specified by the [key] as nullable [UShort]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawUShort(
    key: String,
) : UShort? {
    return withdraw(
        key = key
    )?.toUShortOrNull()
}

/**
 * Method used to withdraw safeguarded data
 *
 * @param key The key of the safeguarded data to withdraw
 *
 * @return the safeguarded data specified by the [key] as nullable [Byte]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawByte(
    key: String,
) : Byte? {
    return withdraw(
        key = key
    )?.toByteOrNull()
}

/**
 * Method used to withdraw safeguarded data
 *
 * @param key The key of the safeguarded data to withdraw
 *
 * @return the safeguarded data specified by the [key] as nullable [UByte]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawUByte(
    key: String,
) : UByte? {
    return withdraw(
        key = key
    )?.toUByteOrNull()
}

/**
 * Method used to withdraw safeguarded data
 *
 * @param key The key of the safeguarded data to withdraw
 *
 * @return the safeguarded data specified by the [key] as nullable [Float]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawFloat(
    key: String,
) : Float? {
    return withdraw(
        key = key
    )?.toFloatOrNull()
}

/**
 * Method used to withdraw safeguarded data
 *
 * @param key The key of the safeguarded data to withdraw
 *
 * @return the safeguarded data specified by the [key] as nullable [Double]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawDouble(
    key: String,
) : Double? {
    return withdraw(
        key = key
    )?.toDoubleOrNull()
}

/**
 * Method used to withdraw safeguarded data
 *
 * @param key The key of the safeguarded data to withdraw
 *
 * @return the safeguarded data specified by the [key] as nullable [Boolean]
 */
// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawBoolean(
    key: String,
) : Boolean? {
    return withdraw(
        key = key
    )?.toBooleanStrictOrNull()
}