package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.Digest
import com.tecknobit.kassaforte.key.genspec.Digest.*
import com.tecknobit.kassaforte.services.OAEPWith.*

/**
 * Method used to associate a [Digest] value with the related [OAEPWith]
 *
 * @return the related OAEP-with value as [OAEPWith]
 */
@Returner
// FIXME: TO REINTEGRATE INTO OAEPWith ENUM AS COMPANION METHOD WHEN ANDROID WILL SUPPORT OTHER OAEP ENTRY AND NOT JUST
// OAEPWithSHA1AndMGF1Padding OR OAEPWithSHA256AndMGF1Padding
actual fun Digest.oaepWithValue(): OAEPWith {
    return when (this) {
        SHA1 -> OAEPWithSHA1AndMGF1Padding
        SHA224 -> OAEPWithSHA224AndMGF1Padding
        SHA256 -> OAEPWithSHA256AndMGF1Padding
        SHA384 -> OAEPWithSHA384AndMGF1Padding
        SHA512 -> OAEPWithSHA512AndMGF1Padding
        else -> throw IllegalArgumentException("No corresponding OAEP-with value")
    }
}