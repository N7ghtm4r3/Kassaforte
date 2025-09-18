package com.tecknobit.kassaforte.key.genspec

class AsymmetricKeyGenSpec(
    override val keySize: KeySize,
    override val encryptionPadding: EncryptionPaddingType = EncryptionPaddingType.NONE,
    val digest: DigestType? = null,
) : KassaforteKeyGenSpec