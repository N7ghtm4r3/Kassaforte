package com.tecknobit.kassaforte.enums

import com.tecknobit.equinoxcore.annotations.RequiresDocumentation
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.Algorithm.*

@RequiresDocumentation(
    additionalNotes = "TO INSERT SINCE Revision Two"
)
enum class Hash(
    val value: String,
) {

    SHA1("SHA-1"),

    SHA256("SHA-256"),

    SHA384("SHA-384"),

    SHA512("SHA-512");

    companion object {

        @Returner
        fun Algorithm.resolveHash(): Hash {
            return when (this) {
                HMAC_SHA1 -> SHA1
                HMAC_SHA256 -> SHA256
                HMAC_SHA384 -> SHA384
                HMAC_SHA512 -> SHA512
                else -> throw IllegalArgumentException("No corresponding hash value with the provided algorithm")
            }
        }

    }

}