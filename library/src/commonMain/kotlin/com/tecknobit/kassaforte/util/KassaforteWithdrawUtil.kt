package com.tecknobit.kassaforte.util

import com.tecknobit.kassaforte.Kassaforte

// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawInt(
    key: String,
) : Int? {
    return withdraw(
        key = key
    )?.toIntOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawUInt(
    key: String,
) : UInt? {
    return withdraw(
        key = key
    )?.toUIntOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawLong(
    key: String,
) : Long? {
    return withdraw(
        key = key
    )?.toLongOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawULong(
    key: String,
) : ULong? {
    return withdraw(
        key = key
    )?.toULongOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawShort(
    key: String,
) : Short? {
    return withdraw(
        key = key
    )?.toShortOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawUShort(
    key: String,
) : UShort? {
    return withdraw(
        key = key
    )?.toUShortOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawByte(
    key: String,
) : Byte? {
    return withdraw(
        key = key
    )?.toByteOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawUByte(
    key: String,
) : UByte? {
    return withdraw(
        key = key
    )?.toUByteOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawFloat(
    key: String,
) : Float? {
    return withdraw(
        key = key
    )?.toFloatOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawDouble(
    key: String,
) : Double? {
    return withdraw(
        key = key
    )?.toDoubleOrNull()
}

// TODO TO ANNOTATE WITH @Returner
suspend fun Kassaforte.withdrawBoolean(
    key: String,
) : Boolean? {
    return withdraw(
        key = key
    )?.toBooleanStrictOrNull()
}