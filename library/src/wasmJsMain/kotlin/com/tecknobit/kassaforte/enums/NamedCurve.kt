package com.tecknobit.kassaforte.enums

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.KeySize

enum class NamedCurve(
    val value: String,
) {

    P256("P-256"),

    P384("P-384"),

    P521("P-521");

    companion object {

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