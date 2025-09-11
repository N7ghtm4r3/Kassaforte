package com.teckonobit.kassaforte.keys

data class AsymmetricKey(
    val publicKey: String,
    val privateKey: String
): KassaforteKey