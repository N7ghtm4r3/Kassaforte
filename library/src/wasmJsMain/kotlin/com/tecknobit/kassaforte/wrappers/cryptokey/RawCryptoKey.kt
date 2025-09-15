package com.tecknobit.kassaforte.wrappers.cryptokey

external interface RawCryptoKey : CryptoKey {

    val alias: String

    val keyData: String

}