package com.tecknobit.kassaforte.key.genspec

data class SymmetricKeyGenSpec(
    override val keySize: KeySize = KeySize.S128,
    val blockMode: BlockModeType,
    override val encryptionPadding: EncryptionPaddingType,
) : KassaforteKeyGenSpec

