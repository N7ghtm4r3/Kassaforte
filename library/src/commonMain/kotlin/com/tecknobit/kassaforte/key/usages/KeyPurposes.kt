package com.tecknobit.kassaforte.key.usages

import kotlinx.serialization.Serializable

@Serializable
data class KeyPurposes(
    val canEncrypt: Boolean = false,
    val canDecrypt: Boolean = false,
    val canSign: Boolean = false,
    val canVerify: Boolean = false,
    val canWrapKey: Boolean = false,
    val canAgree: Boolean = false
)