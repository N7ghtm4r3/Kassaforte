package com.tecknobit.kassaforte.key.genspec;

enum class SignaturePaddingType(
    val value: String
) {
    
    RSA_PKCS1("PKCS1"),
    
    RSA_PSS("PSS");
}