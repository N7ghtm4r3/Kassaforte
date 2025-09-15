package com.tecknobit.kassaforte.wrappers.crypto.key

external interface RawCryptoKey : CryptoKey {

    val alias: String

    val keyData: String

}