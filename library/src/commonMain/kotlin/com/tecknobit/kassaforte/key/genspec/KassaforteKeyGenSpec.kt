package com.tecknobit.kassaforte.key.genspec

/**
 * The `KassaforteKeyGenSpec` interface represent the configuration the generating key must have
 *
 * @author Tecknobit - N7ghtm4r3
 */
sealed interface KassaforteKeyGenSpec {

    /**
     * `keySize` The size of the key
     */
    val keySize: KeySize

    /**
     * `encryptionPadding` The encryption padding the key can use
     */
    val encryptionPadding: EncryptionPadding

}