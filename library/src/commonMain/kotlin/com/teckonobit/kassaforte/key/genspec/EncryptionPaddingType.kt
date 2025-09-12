package com.teckonobit.kassaforte.key.genspec;

enum class EncryptionPaddingType(
    val value: String
) {
    
    NONE("NoPadding"),

    PKCS5("PKCS5Padding"),
    
    PKCS7("PKCS7Padding"),
    
    RSA_PKCS1("PKCS1Padding"),
    
    RSA_OAEP("OAEPPadding");
}