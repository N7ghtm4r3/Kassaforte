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

enum class KeyType(
    val type: CFStringRef?,
) {

    kAttrKeyRSA(kSecAttrKeyTypeRSA),

    kAttrKeyECSEC(kSecAttrKeyTypeECSECPrimeRandom);

    companion object {

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