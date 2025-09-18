package com.tecknobit.kassaforte.services.impls

import com.tecknobit.kassaforte.key.genspec.AlgorithmType
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyDetailsSheet
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.ALIAS_ALREADY_TAKEN_ERROR
import com.tecknobit.kassaforte.services.helpers.KassaforteServiceImplManager
import com.tecknobit.kassaforte.services.helpers.KassaforteServiceImplManager.Companion.encode64
import kotlinx.serialization.Serializable
import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class KassaforteAsymmetricServiceImpl actual constructor() : KassaforteServiceImpl() {

    private val serviceImplManager = KassaforteServiceImplManager<KeyInfo>()

    actual fun generateKey(
        algorithmType: AlgorithmType,
        alias: String,
        keyGenSpec: AsymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        if (aliasExists(alias))
            throw IllegalAccessException(ALIAS_ALREADY_TAKEN_ERROR)
        val algorithm = algorithmType.value
        val keyPairGenerator = KeyPairGenerator.getInstance(algorithm)
        keyPairGenerator.initialize(keyGenSpec.keySize.bitCount)
        val keyPair = keyPairGenerator.genKeyPair()
        serviceImplManager.storeKeyData(
            alias = alias,
            keyInfo = KeyInfo(
                keyPurposes = purposes,
                keyPair = keyPair
            ),
            encode64 = false
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
        TODO("Not yet implemented")
    }

    actual override fun deleteKey(
        alias: String,
    ) {
    }

    @Serializable
    private data class KeyInfo(
        override val keyPurposes: KeyPurposes,
        override val key: String,
        val publicKey: String,
    ) : KeyDetailsSheet<String> {

        constructor(
            keyPurposes: KeyPurposes,
            keyPair: KeyPair,
        ) : this(
            keyPurposes = keyPurposes,
            key = keyPair.private.encode64(),
            publicKey = keyPair.public.encode64()
        )

    }

}