package com.tecknobit.kassaforte.services.impls

import android.security.keystore.KeyGenParameterSpec
import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.kassaforte.key.genspec.AlgorithmType
import com.tecknobit.kassaforte.key.genspec.BlockModeType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
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
            AlgorithmType.AES.value,
            ANDROID_KEYSTORE
        )
        val genSpec = KeyGenParameterSpec.Builder(
            alias,
            serviceImplManager.resolvePurposes(
                keyPurposes = purposes
            )
        ).run {
            setBlockModes(keyGenSpec.blockMode.value)
            setEncryptionPaddings(keyGenSpec.encryptionPadding.value)
            setKeySize(keyGenSpec.keySize.bitCount)
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

    actual fun getKey(
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

    actual override fun deleteKey(
        alias: String,
    ) {
        serviceImplManager.removeKey(
            alias = alias
        )
    }

}