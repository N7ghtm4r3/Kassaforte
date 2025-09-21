package com.tecknobit.kassaforte.key.usages

import com.tecknobit.kassaforte.key.usages.KeyOperation.*

/**
 * The `KeyDetailsSheet` interface allows to store with the generated key extra information related to it such as the
 * usages the key can be used
 *
 * @param T the type of the key
 *
 * @author Tecknobit - N7ghtm4r3
 */
interface KeyDetailsSheet<T> {

    /**
     * `keyPurposes` the purposes used to generate the key
     */
    val keyPurposes: KeyPurposes

    /**
     * `key` the generated key
     */
    val key: T

    /**
     * `canEncrypt` whether the key can be used to encrypt data
     */
    val canEncrypt: Boolean
        get() = keyPurposes.canEncrypt

    /**
     * `canDecrypt` whether the key can be used to decrypt data
     */
    val canDecrypt: Boolean
        get() = keyPurposes.canDecrypt

    /**
     * `canSign` whether the key can be used to sign data
     */
    val canSign: Boolean
        get() = keyPurposes.canSign

    /**
     * `canVerify` whether the key can be used to verify data
     */
    val canVerify: Boolean
        get() = keyPurposes.canVerify

    /**
     * `canAgree` whether the key can be used in a key agreement protocol
     * to derive a shared secret
     */
    val canAgree: Boolean
        get() = keyPurposes.canAgree

    /**
     * `canWrapKey` whether the key can be used to wrap other key
     */
    val canWrapKey: Boolean
        get() = keyPurposes.canWrapKey

    /**
     * Validator method used to check whether the key can be used to perform the specified [keyOperation]
     *
     * @param keyOperation The operation to check
     *
     * @return whether the operation can be performed by the [key] as [Boolean]
     */
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