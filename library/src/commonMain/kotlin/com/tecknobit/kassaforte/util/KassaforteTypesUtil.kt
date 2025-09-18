package com.tecknobit.kassaforte.util

const val UNSUPPORTED_TYPE = "Type not supported"

// TODO: ANNOTATE WITH @Validator
internal fun checkIfIsSupportedType(
    data: Any
) {
    require(data is String || data is Number || data is Boolean) { UNSUPPORTED_TYPE }
}