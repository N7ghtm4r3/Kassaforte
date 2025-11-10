package com.tecknobit.kassaforte.util

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.equinoxcore.annotations.Wrapper
import kotlin.io.encoding.Base64

/**
 * Method used to encode the data in a readable format after a [com.tecknobit.kassaforte.services.KassaforteKeysService]'s
 * operation such encryption
 *
 * @param data The source data to encode
 *
 * @return the encoded data as [String]
 *
 * @since Revision Two
 */
@Wrapper
@Returner
fun encode(
    data: Any,
): String {
    return encode(
        data = data.toString().encodeToByteArray()
    )
}

/**
 * Method used to encode the data in a readable format after a [com.tecknobit.kassaforte.services.KassaforteKeysService]'s
 * operation such encryption
 *
 * @param data The source data to encode
 *
 * @return the encoded data as [String]
 *
 * @since Revision Two
 */
@Returner
fun encode(
    data: ByteArray,
): String {
    return Base64.encode(
        source = data
    )
}

/**
 * Method used to decode the previously encoded data with the [encode] method
 *
 * @param source The source data to decode
 *
 * @return the decoded data as [ByteArray]
 *
 * @since Revision Two
 */
@Wrapper
@Returner
fun decode(
    source: String,
): ByteArray {
    return decode(
        source = source.encodeToByteArray()
    )
}

/**
 * Method used to decode the previously encoded data with the [encode] method
 *
 * @param source The source data to decode
 *
 * @return the decoded data as [ByteArray]
 *
 * @since Revision Two
 */
@Returner
fun decode(
    source: ByteArray,
): ByteArray {
    return Base64.decode(
        source = source
    )
}

/**
 * Method used to encode the data to perform a key operation checking also whether the type
 * is currently supported using the [checkIfIsSupportedType] validator
 *
 * @return the encoded data as [ByteArray]
 *
 * @since Revision Two
 */
@Returner
fun Any.encodeForKeyOperation(): ByteArray {
    checkIfIsSupportedType(this)
    return this.toString().encodeToByteArray()
}