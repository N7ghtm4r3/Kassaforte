package com.tecknobit.kassaforte.key.genspec

/**
 * The `AsymmetricKeyGenSpec` class represent the configuration the generating asymmetric keys must have
 *
 * @property keySize The size of the key
 * @property encryptionPadding The encryption padding the keys can use
 * @property digest The digest the keys can use
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteKeyGenSpec
 */
data class AsymmetricKeyGenSpec(
    override val keySize: KeySize,
    override val encryptionPadding: EncryptionPaddingType = EncryptionPaddingType.NONE,
    val digest: DigestType? = null,
) : KassaforteKeyGenSpec