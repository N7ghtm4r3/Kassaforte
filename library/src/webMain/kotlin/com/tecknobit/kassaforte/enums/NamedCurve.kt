package com.tecknobit.kassaforte.enums

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.KeySize

/**
 * These are the supported named curves
 *
 * @param value The native value of the named curve
 */
enum class NamedCurve(
    val value: String,
) {

    /**
     * `P256` NIST curve P-256 (secp256r1)
     */
    P256("P-256"),

    /**
     * `P384` NIST curve P-384 (secp384r1)
     */
    P384("P-384"),

    /**
     * `P521` NIST curve P-521 (secp521r1)
     */
    P521("P-521");

    companion object {

        /**
         * Method used to convert a [KeySize] into related [NamedCurve]
         *
         * @return the value of the native curve based on the [KeySize] as [NamedCurve]
         *
         * @throws IllegalArgumentException when the named curve source is not valid
         */
        @Returner
        fun KeySize.toNamedCurve(): NamedCurve {
            return when (this) {
                KeySize.S256 -> P256
                KeySize.S384 -> P384
                KeySize.S521 -> P521
                else -> {
                    throw IllegalArgumentException("Invalid named curve source")
                }
            }
        }

    }

}