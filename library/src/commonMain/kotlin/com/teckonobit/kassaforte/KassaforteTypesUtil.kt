package com.teckonobit.kassaforte

import com.tecknobit.equinoxcore.annotations.Returner

internal fun checkIfIsSupportedType(
    data: Any
) {
    require(
        value = data is Number || data is String || data is Boolean || data is ByteArray
    ) { "Type not supported" }
}

@Returner
fun Kassaforte.withdrawInt(
    key: String
) : Int? {
    return withdraw(
        key = key
    )?.toIntOrNull()
}

@Returner
fun Kassaforte.withdrawUInt(
    key: String
) : UInt? {
    return withdraw(
        key = key
    )?.toUIntOrNull()
}

@Returner
fun Kassaforte.withdrawLong(
    key: String
) : Long? {
    return withdraw(
        key = key
    )?.toLongOrNull()
}

@Returner
fun Kassaforte.withdrawULong(
    key: String
) : ULong? {
    return withdraw(
        key = key
    )?.toULongOrNull()
}

@Returner
fun Kassaforte.withdrawShort(
    key: String
) : Short? {
    return withdraw(
        key = key
    )?.toShortOrNull()
}

@Returner
fun Kassaforte.withdrawUShort(
    key: String
) : UShort? {
    return withdraw(
        key = key
    )?.toUShortOrNull()
}

@Returner
fun Kassaforte.withdrawByte(
    key: String
) : Byte? {
    return withdraw(
        key = key
    )?.toByteOrNull()
}

@Returner
fun Kassaforte.withdrawUByte(
    key: String
) : UByte? {
    return withdraw(
        key = key
    )?.toUByteOrNull()
}

@Returner
fun Kassaforte.withdrawFloat(
    key: String
) : Float? {
    return withdraw(
        key = key
    )?.toFloatOrNull()
}

@Returner
fun Kassaforte.withdrawDouble(
    key: String
) : Double? {
    return withdraw(
        key = key
    )?.toDoubleOrNull()
}

@Returner
fun Kassaforte.withdrawBoolean(
    key: String
) : Boolean? {
    return withdraw(
        key = key
    )?.toBooleanStrictOrNull()
}