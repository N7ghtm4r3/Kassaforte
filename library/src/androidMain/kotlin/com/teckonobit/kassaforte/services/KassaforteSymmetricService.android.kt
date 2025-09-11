package com.teckonobit.kassaforte.services

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.*
import com.tecknobit.equinoxcore.annotations.Assembler
import com.teckonobit.kassaforte.keyspec.SymmetricKeyGenSpec
import com.teckonobit.kassaforte.keyspec.convert
import javax.crypto.KeyGenerator

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class KassaforteSymmetricService actual constructor(
    private val alias: String
) : KassaforteKeysService<SymmetricKeyGenSpec>() {

    companion object {

        private const val ANDROID_KEYSTORE = "AndroidKeyStore"

    }

    actual override fun generate(
        keyGenSpec: SymmetricKeyGenSpec
    ) {
        val keyGenerator = KeyGenerator.getInstance(
            keyGenSpec.algorithm.value,
            ANDROID_KEYSTORE
        )
        keyGenSpec.keySize?.let { keySize ->
            keyGenerator.init(keySize)
        }
        val purposes = resolvePurposes(
            keyGenSpec = keyGenSpec
        )
        val genSpec = KeyGenParameterSpec.Builder(
            alias,
            purposes
        ).run {
            setBlockModes(*keyGenSpec.blockModes.convert())
            setDigests(*keyGenSpec.digests.convert())
            setEncryptionPaddings(*keyGenSpec.encryptionPaddings.convert())
            build()
        }
        keyGenerator.init(genSpec)
        keyGenerator.generateKey()
    }

    @Assembler
    private fun resolvePurposes(
        keyGenSpec: SymmetricKeyGenSpec
    ): Int {
        var purposes = 0
        val keyPurposes = keyGenSpec.purposes
        if(keyPurposes.canEncrypt)
            purposes = purposes or PURPOSE_ENCRYPT
        if(keyPurposes.canDecrypt)
            purposes = purposes or PURPOSE_DECRYPT
        if(keyPurposes.canSign)
            purposes = purposes or PURPOSE_SIGN
        if(keyPurposes.canVerify)
            purposes = purposes or PURPOSE_VERIFY
        if(keyPurposes.canWrapKey && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            purposes = purposes or PURPOSE_WRAP_KEY
        if(keyPurposes.canAgree && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            purposes = purposes or PURPOSE_AGREE_KEY
        if(purposes == 0)
            throw IllegalStateException("Key purposes not valid")
        return purposes
    }

    actual override fun encrypt(
        data: Any
    ): String {
        TODO("Not yet implemented")
    }

    actual override fun decrypt(
        data: String
    ): Any {
        TODO("Not yet implemented")
    }

    actual override fun delete() {

    }

}