package com.tecknobit.kassaforte.services.impls

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.BlockMode
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyDetailsSheet
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.KassaforteKeysService
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.KEY_CANNOT_PERFORM_OPERATION_ERROR
import com.tecknobit.kassaforte.services.helpers.KassaforteServiceImplManager
import com.tecknobit.kassaforte.services.helpers.KassaforteServiceImplManager.Companion.encode64
import kotlinx.serialization.Serializable
import java.security.Key
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class KassaforteSymmetricServiceImpl actual constructor() : KassaforteServiceImpl() {

    private companion object {

        const val PKCS5 = "PKCS5Padding"

    }

    private val serviceImplManager = KassaforteServiceImplManager.getInstance(
        serializer = KeyInfo.serializer()
    )

    actual fun generateKey(
        alias: String,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        if (aliasExists(alias))
            throw IllegalAccessException(KassaforteKeysService.ALIAS_ALREADY_TAKEN_ERROR)
        val algorithm = Algorithm.AES.value
        val keyGenerator = KeyGenerator.getInstance(algorithm)
        keyGenerator.init(keyGenSpec.keySize.bitCount)
        val key = keyGenerator.generateKey()
        serviceImplManager.storeKeyData(
            alias = alias,
            keyInfo = KeyInfo(
                algorithm = algorithm,
                key = key,
                keyPurposes = purposes
            )
        )
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
        val keyInfo = serviceImplManager.retrieveKey(
            alias = alias
        )
        if (!keyInfo.canPerform(keyOperation))
            throw RuntimeException(KEY_CANNOT_PERFORM_OPERATION_ERROR.format(keyOperation))
        return keyInfo.resolveKey()
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
            transformation += padding.adapt()
        }
        return transformation
    }

    @Returner
    private fun EncryptionPadding.adapt(): String {
        return "/" + when (this) {
            EncryptionPadding.PKCS7 -> PKCS5
            else -> this.value
        }
    }

    actual override fun deleteKey(
        alias: String,
    ) {
        serviceImplManager.removeKey(
            alias = alias
        )
    }

    @Serializable
    private data class KeyInfo(
        val algorithm: String,
        override val keyPurposes: KeyPurposes,
        override val key: String,
    ) : KeyDetailsSheet<String> {

        constructor(
            algorithm: String,
            key: Key,
            keyPurposes: KeyPurposes,
        ) : this(
            algorithm = algorithm,
            key = key.encode64(),
            keyPurposes = keyPurposes
        )

        @Returner
        fun resolveKey(): Key = SecretKeySpec(
            Base64.decode(key.encodeToByteArray()),
            algorithm
        )

    }

}