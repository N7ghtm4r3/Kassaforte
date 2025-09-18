package com.tecknobit.kassaforte.key.usages

enum class KeyOperation {
    
    ENCRYPT,
    
    DECRYPT,
    
    SIGN,
    
    VERIFY,
    
    AGREE,
    
    WRAP,

    OBTAIN_KEY;

    companion object {

        // TODO: TO ANNOTATE WITH @Returner
        fun KeyOperation.checkIfRequiresPublicKey(): Boolean {
            return when (this) {
                ENCRYPT -> true
                else -> false
            }
        }

    }
    
}