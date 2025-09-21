package com.tecknobit.kassaforte.services.impls

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.BlockMode
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.KassaforteKeysService
import com.tecknobit.kassaforte.services.helpers.KassaforteServiceImplManager
import com.tecknobit.kassaforte.services.helpers.KassaforteServiceImplManager.Companion.ANDROID_KEYSTORE
import java.security.Key
import javax.crypto.KeyGenerator

// TODO: TO WARN ABOUT SET allowBackup=false
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class KassaforteSymmetricServiceImpl actual constructor() : KassaforteServiceImpl() {

    private val serviceImplManager = KassaforteServiceImplManager()

    actual fun generateKey(
        alias: String,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        if (aliasExists(alias))
            throw IllegalAccessException(KassaforteKeysService.ALIAS_ALREADY_TAKEN_ERROR)
        val keyGenerator = KeyGenerator.getInstance(
            Algorithm.AES.value,
            ANDROID_KEYSTORE
        )
        val genSpec = serviceImplManager.resolveGenSpec(
            alias = alias,
            keyGenSpec = keyGenSpec,
            purposes = purposes
        ).run {
            setBlockModes(keyGenSpec.blockMode.value)
            setEncryptionPaddings(keyGenSpec.encryptionPadding.value)
            build()
        }
        keyGenerator.init(genSpec)
        keyGenerator.generateKey()
    }

    actual override fun aliasExists(
        alias: String,
    ): Boolean {
        return serviceImplManager.isAliasTaken(
            alias = alias
        )
    }

    actual override fun getKey(
        alias: String,
        keyOperation: KeyOperation,
    ): Key {
        return serviceImplManager.retrieveKey(
            alias = alias
        )
    }

    @Assembler
    actual fun resolveTransformation(
        algorithm: String,
        blockMode: BlockMode?,
        padding: EncryptionPadding?,
    ): String {
        var transformation = algorithm
        blockMode?.let {
            transformation += "/${blockMode.value}"
        }
        padding?.let {
            transformation += "/${padding.value}"
        }
        return transformation
    }

    actual override fun deleteKey(
        alias: String,
    ) {
        serviceImplManager.removeKey(
            alias = alias
        )
    }

}