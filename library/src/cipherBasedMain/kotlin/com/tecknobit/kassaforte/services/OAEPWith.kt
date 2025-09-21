package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.Digest
import com.tecknobit.kassaforte.key.genspec.Digest.*

/**
 * These are the supported OAEP-with values used with the [com.tecknobit.kassaforte.key.genspec.EncryptionPadding.RSA_OAEP]
 * algorithm
 *
 * @property value The name of the OAEP-with value
 */
enum class OAEPWith(
    val value: String,
) {

    /**
     * `OAEPWithSHA1AndMGF1Padding` OAEP padding using `SHA-1` as the hash function and `MGF1` mask generation
     */
    OAEPWithSHA1AndMGF1Padding("OAEPWithSHA-1AndMGF1Padding"),

    /**
     * `OAEPWithSHA224AndMGF1Padding` OAEP padding using `SHA-224` as the hash function and `MGF1` mask generation
     */
    OAEPWithSHA224AndMGF1Padding("OAEPWithSHA-224AndMGF1Padding"),

    /**
     * `OAEPWithSHA256AndMGF1Padding` OAEP padding using `SHA-256` as the hash function and `MGF1` mask generation
     */
    OAEPWithSHA256AndMGF1Padding("OAEPWithSHA-256AndMGF1Padding"),

    /**
     * `OAEPWithSHA384AndMGF1Padding` OAEP padding using `SHA-384` as the hash function and `MGF1` mask generation
     */
    OAEPWithSHA384AndMGF1Padding("OAEPWithSHA-384AndMGF1Padding"),

    /**
     * `OAEPWithSHA512AndMGF1Padding` OAEP padding using `SHA-512` as the hash function and `MGF1` mask generation
     */
    OAEPWithSHA512AndMGF1Padding("OAEPWithSHA-512AndMGF1Padding");

    companion object {

        /**
         * Method used to associate a [Digest] value with the related [OAEPWith]
         *
         * @return the related OAEP-with value as [OAEPWith]
         */
        @Returner
        fun Digest.oaepWithValue(): OAEPWith {
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