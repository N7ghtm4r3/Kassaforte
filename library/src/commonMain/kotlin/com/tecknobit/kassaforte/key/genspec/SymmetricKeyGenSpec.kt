package com.tecknobit.kassaforte.key.genspec

/**
 * The `SymmetricKeyGenSpec` class represent the configuration the generating symmetric key must have
 *
 * @property keySize The size of the key
 * @property encryptionPadding The encryption padding the keys can use
 * @property blockMode The mode of the block the key supports (`CBC`, `CTR` or `GCM`)
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteKeyGenSpec
 */
data class SymmetricKeyGenSpec(
    override val keySize: KeySize = KeySize.S128,
    override val encryptionPadding: EncryptionPadding,
    val blockMode: BlockMode,
) : KassaforteKeyGenSpec

