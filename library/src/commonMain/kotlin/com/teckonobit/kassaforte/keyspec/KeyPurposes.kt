package com.teckonobit.kassaforte.keyspec

data class KeyPurposes(
    val canEncrypt: Boolean = false,
    val canDecrypt: Boolean = false,
    val canSign: Boolean = false,
    val canVerify: Boolean = false,
    val canWrapKey: Boolean = false,
    val canAgree: Boolean = false
)