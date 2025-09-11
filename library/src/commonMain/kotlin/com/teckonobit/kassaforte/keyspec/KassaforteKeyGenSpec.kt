package com.teckonobit.kassaforte.keyspec

sealed interface KassaforteKeyGenSpec {

    val algorithm: AlgorithmType

    val keySize: Int?

    val purposes: KeyPurposes

    val digests: Array<DigestType>

    val encryptionPaddings: Array<EncryptionPaddingType>

}