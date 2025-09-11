package com.teckonobit.kassaforte

const val UNSUPPORTED_TYPE = "Type not supported"

internal fun checkIfIsSupportedType(
    data: Any
) {
    require(data is String || data is Number || data is Boolean) { UNSUPPORTED_TYPE }
}