package com.tecknobit.kassaforte.key.usages

import com.tecknobit.kassaforte.key.usages.KeyOperation.*

interface KeyDetailsSheet<T> {

    val keyPurposes: KeyPurposes

    val key: T

    val canEncrypt: Boolean
        get() = keyPurposes.canEncrypt

    val canDecrypt: Boolean
        get() = keyPurposes.canDecrypt

    val canSign: Boolean
        get() = keyPurposes.canSign

    val canVerify: Boolean
        get() = keyPurposes.canVerify

    val canAgree: Boolean
        get() = keyPurposes.canAgree

    val canWrapKey: Boolean
        get() = keyPurposes.canWrapKey

    // TODO: TO ANNOTATE WITH @Validator
    fun canPerform(
        keyOperation: KeyOperation,
    ): Boolean {
        return when (keyOperation) {
            ENCRYPT -> canEncrypt
            DECRYPT -> canDecrypt
            SIGN -> canSign
            VERIFY -> canVerify
            AGREE -> canAgree
            WRAP -> canWrapKey
            OBTAIN_KEY -> true
        }
    }

}