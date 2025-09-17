package com.tecknobit.kassaforte.key.genspec

sealed interface KassaforteKeyGenSpec {

    val keySize: KeySize

    val encryptionPadding: EncryptionPaddingType

}