package com.tecknobit.kassaforte.key.usages

/**
 * These are the operations a generated key can do
 */
enum class KeyOperation {

    /**
     * `ENCRYPT` the operation where the key is used to encrypt data
     */
    ENCRYPT,

    /**
     * `DECRYPT` the operation where the key is used to decrypt data
     */
    DECRYPT,

    /**
     * `SIGN` the operation where the key is used to sign data
     */
    SIGN,

    /**
     * `VERIFY` the operation where the key is used to verify data
     */
    VERIFY,

    /**
     * `AGREE` the operation where the key is used in a key agreement protocol
     * to derive a shared secret (e.g., Diffie–Hellman, ECDH)
     */
    AGREE,

    /**
     * `WRAP` the operation where the key is used to wrap other key
     */
    WRAP,

    /**
     * `OBTAIN_KEY` the operation where is requested the key
     */
    OBTAIN_KEY;

    companion object {

        /**
         * Method used to check whether in an asymmetric algorithm is required to use the public key instead the private
         * one
         *
         * @return whether the key required is the public one based on the performing [KeyOperation]
         */
        // TODO: TO ANNOTATE WITH @Returner
        fun KeyOperation.checkIfRequiresPublicKey(): Boolean {
            return when (this) {
                ENCRYPT, VERIFY -> true
                else -> false
            }
        }

    }
    
}