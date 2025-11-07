package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.Digest
import com.tecknobit.kassaforte.key.genspec.Digest.SHA256
import com.tecknobit.kassaforte.services.OAEPWith.OAEPWithSHA1AndMGF1Padding
import com.tecknobit.kassaforte.services.OAEPWith.OAEPWithSHA256AndMGF1Padding
import com.tecknobit.kassaforte.services.helpers.isStrongBoxAvailable

/**
 * Method used to associate a [Digest] value with the related [OAEPWith]
 *
 * @return the related OAEP-with value as [OAEPWith]
 */
@Returner
// FIXME: TO REMOVE WHEN ANDROID WILL SUPPORT OTHER OAEP ENTRY AND NOT JUST OAEPWithSHA1AndMGF1Padding OR OAEPWithSHA256AndMGF1Padding
actual fun Digest.oaepWithValue(): OAEPWith {
    return if (isStrongBoxAvailable() && this == SHA256)
        OAEPWithSHA256AndMGF1Padding
    else
        OAEPWithSHA1AndMGF1Padding
}