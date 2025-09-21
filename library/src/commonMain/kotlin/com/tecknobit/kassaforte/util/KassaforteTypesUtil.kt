package com.tecknobit.kassaforte.util

/**
 * `UNSUPPORTED_TYPE` the error message to display when the type is not supported
 */
const val UNSUPPORTED_TYPE = "Type not supported"

/**
 * Validator method used to check whether the type of the [data] is supported to be encrypted
 *
 * @param data The data to check its type
 */
// TODO: ANNOTATE WITH @Validator
internal fun checkIfIsSupportedType(
    data: Any
) {
    require(data is String || data is Number || data is Boolean) { UNSUPPORTED_TYPE }
}