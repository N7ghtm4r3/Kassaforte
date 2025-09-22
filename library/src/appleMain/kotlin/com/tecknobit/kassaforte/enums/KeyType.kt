@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte.enums

import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.Algorithm.EC
import com.tecknobit.kassaforte.key.genspec.Algorithm.RSA
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.INVALID_ASYMETRIC_ALGORITHM
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreFoundation.CFStringRef
import platform.Security.kSecAttrKeyTypeECSECPrimeRandom
import platform.Security.kSecAttrKeyTypeRSA

/**
 * These are the supported types for the keys to generate and to use
 *
 * @param type The native type of the key
 */
enum class KeyType(
    val type: CFStringRef?,
) {

    /**
     * `kAttrKeyRSA` a key that is based on the `RSA` algorithm
     */
    kAttrKeyRSA(kSecAttrKeyTypeRSA),

    /**
     * `kAttrKeyECSEC` a key that is based on the `EC` algorithm (`ECSEC` spec)
     */
    kAttrKeyECSEC(kSecAttrKeyTypeECSECPrimeRandom);

    companion object {

        /**
         * Method used to convert a [Algorithm] into related native key type
         *
         * @return the native key type related to an algorithm as [KeyType]
         */
        // TODO: TO ANNOTATE WITH @Returner
        fun Algorithm.toKeyType(): KeyType {
            return when (this) {
                RSA -> kAttrKeyRSA
                EC -> kAttrKeyECSEC
                else -> {
                    throw IllegalArgumentException(INVALID_ASYMETRIC_ALGORITHM)
                }
            }
        }

    }


}