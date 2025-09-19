package com.tecknobit.kassaforte.wrappers.crypto.key.raw

import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey

external interface RawCryptoKey : CryptoKey {

    val alias: String

    val keyData: String

}