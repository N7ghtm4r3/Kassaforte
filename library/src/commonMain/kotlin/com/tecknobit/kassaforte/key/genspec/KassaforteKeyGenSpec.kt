package com.tecknobit.kassaforte.key.genspec

sealed interface KassaforteKeyGenSpec {

    val algorithm: AlgorithmType

    val keySize: Int?

    val digests: Array<DigestType>

    val encryptionPaddings: Array<EncryptionPaddingType>

}