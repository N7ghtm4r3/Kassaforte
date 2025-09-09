package com.teckonobit.kassaforte

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPairGenerator

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class KassaforteProvider actual constructor(
    private val alias: String
) {

    private companion object {

        const val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"

    }

    // TODO: ALLOW TO USE OTHER ALGORITHM
    // TODO: TO DOCU ABOUT THE COMBINATION OF PURPOSES AND DIGEST AND THE ALGORITHM
    actual fun generateKeyPair() {
        val keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            ANDROID_KEYSTORE_PROVIDER
        )
        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        ).run {
            setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            build()
        }
        keyPairGenerator.initialize(parameterSpec)
        keyPairGenerator.genKeyPair()
    }

    actual fun readAliases(): Collection<String> {
        TODO("Not yet implemented")
    }

    actual fun sign(
        data: Any
    ) {
    }

    actual fun verify(
        data: Any
    ) {
    }

}