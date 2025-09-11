package com.teckonobit.kassaforte.keyspec;

enum class EncryptionPaddingType(
    val value: String
) {
    
    NONE("NoPadding"),
    
    PKCS7("PKCS7Padding"),
    
    RSA_PKCS1("PKCS1Padding"),
    
    RSA_OAEP("OAEPPadding");
}