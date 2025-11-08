package com.tecknobit.kassaforte.key.usages

import kotlinx.serialization.Serializable

/**
 * The `KeyPurposes` data class represents the purposes used to generate the key and when the key can be used
 *
 * @property canEncrypt Whether the key can be used to encrypt data
 * @property canDecrypt Whether the key can be used to decrypt data
 * @property canSign Whether the key can be used to sign messages
 * @property canVerify Whether the key can be used to verify messages, note that this purpose will be ignored on symmetric contexts like `HMAC`
 * @property canWrapKey Whether the key can be used in a key agreement protocol
 * to derive a shared secret
 * @property canAgree Whether the key can be used to wrap other key
 *
 * @author Tecknobit - N7ghtm4r3
 */
@Serializable
data class KeyPurposes(
    val canEncrypt: Boolean = false,
    val canDecrypt: Boolean = false,
    val canSign: Boolean = false,
    val canVerify: Boolean = false,
    val canWrapKey: Boolean = false,
    val canAgree: Boolean = false
)