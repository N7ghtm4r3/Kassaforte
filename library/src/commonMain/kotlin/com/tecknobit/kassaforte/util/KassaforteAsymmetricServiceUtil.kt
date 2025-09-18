package com.tecknobit.kassaforte.util

import com.tecknobit.kassaforte.key.genspec.AlgorithmType.RSA

const val UNSUPPORTED_CIPHER_ALGORITHM = "The %s algorithm is not supported to cipher"

// TODO: ANNOTATE WITH @Validator
internal fun checkIfIsSupportedCipherAlgorithm(
    algorithm: String,
) {
    require(algorithm == RSA.value) { UNSUPPORTED_CIPHER_ALGORITHM.replace("%s", algorithm) }
}