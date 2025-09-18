package com.tecknobit.kassaforte.key.genspec

class AsymmetricKeyGen(
    override val keySize: KeySize,
    override val encryptionPadding: EncryptionPaddingType,
) : KassaforteKeyGenSpec