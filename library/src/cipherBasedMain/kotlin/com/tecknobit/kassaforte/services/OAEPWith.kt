package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.DigestType
import com.tecknobit.kassaforte.key.genspec.DigestType.*

enum class OAEPWith(
    val value: String,
) {

    OAEPWithSHA1AndMGF1Padding("OAEPWithSHA-1AndMGF1Padding"),

    OAEPWithSHA224AndMGF1Padding("OAEPWithSHA-224AndMGF1Padding"),

    OAEPWithSHA256AndMGF1Padding("OAEPWithSHA-256AndMGF1Padding"),

    OAEPWithSHA384AndMGF1Padding("OAEPWithSHA-384AndMGF1Padding"),

    OAEPWithSHA512AndMGF1Padding("OAEPWithSHA-512AndMGF1Padding");

    companion object {

        // TODO: CHECK TO MOVE INTO DigestType
        @Returner
        fun DigestType.oaepWithValue(): OAEPWith {
            return when (this) {
                SHA1 -> OAEPWithSHA1AndMGF1Padding
                SHA224 -> OAEPWithSHA224AndMGF1Padding
                SHA256 -> OAEPWithSHA256AndMGF1Padding
                SHA384 -> OAEPWithSHA384AndMGF1Padding
                SHA512 -> OAEPWithSHA512AndMGF1Padding
                else -> throw IllegalArgumentException("No corresponding OAEP-with value")
            }
        }

    }

}