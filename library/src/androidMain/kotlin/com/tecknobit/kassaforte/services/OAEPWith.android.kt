package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.Digest
import com.tecknobit.kassaforte.services.OAEPWith.OAEPWithSHA1AndMGF1Padding

/**
 * Method used to associate a [Digest] value with the related [OAEPWith]
 *
 * @return the related OAEP-with value as [OAEPWith]
 */
@Returner
// FIXME: TO REMOVE WHEN ANDROID WILL SUPPORT OTHER OAEP ENTRY AND NOT JUST OAEPWithSHA1AndMGF1Padding
actual fun Digest.oaepWithValue(): OAEPWith {
    return OAEPWithSHA1AndMGF1Padding
}