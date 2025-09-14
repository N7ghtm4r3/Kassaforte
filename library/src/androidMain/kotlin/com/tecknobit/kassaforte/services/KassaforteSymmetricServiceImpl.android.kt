package com.tecknobit.kassaforte.services

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.*
import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.kassaforte.key.KeyPurposes
import com.tecknobit.kassaforte.key.genspec.BlockModeType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.convert
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.ALIAS_ALREADY_TAKEN_ERROR
import java.security.Key
import java.security.KeyStore
import javax.crypto.KeyGenerator

// TODO: TO WARN ABOUT SET allowBackup=false
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class KassaforteSymmetricServiceImpl actual constructor() {

    companion object {

        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        
    }

    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    actual fun generateKey(
        alias: String,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        if(aliasExists(alias))
            throw IllegalAccessException(ALIAS_ALREADY_TAKEN_ERROR)
        val keyGenerator = KeyGenerator.getInstance(
            keyGenSpec.algorithm.value,
            ANDROID_KEYSTORE
        )
        val genSpec = KeyGenParameterSpec.Builder(
            alias,
            resolvePurposes(
                keyPurposes = purposes
            )
        ).run {
            setBlockModes(keyGenSpec.blockMode.value)
            setDigests(*keyGenSpec.digests.convert())
            setEncryptionPaddings(*keyGenSpec.encryptionPaddings.convert())
            keyGenSpec.keySize?.let { keySize ->
                setKeySize(keySize)
            }
            build()
        }
        keyGenerator.init(genSpec)
        keyGenerator.generateKey()
    }

    actual fun aliasExists(
        alias: String
    ): Boolean {
        return keyStore.isKeyEntry(alias)
    }

    @Assembler
    private fun resolvePurposes(
        keyPurposes: KeyPurposes
    ): Int {
        var purposes = 0
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

    actual fun getKey(
        alias: String,
        keyOperation: KeyOperation
    ): Key {
        return keyStore.getKey(alias, null)
    }

    @Assembler
    internal actual fun resolveTransformation(
        algorithm: String,
        blockModeType: BlockModeType?,
        paddingType: EncryptionPaddingType?,
    ): String {
        var transformation = algorithm
        blockModeType?.let { blockMode ->
            transformation += "/${blockMode.value}"
        }
        paddingType?.let { padding ->
            transformation += "/${padding.value}"
        }
        return transformation
    }

    actual fun deleteKey(
        alias: String
    ) {
        keyStore.deleteEntry(alias)
    }

}