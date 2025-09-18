package com.tecknobit.kassaforte.services.impls

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.AlgorithmType
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyDetailsSheet
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyOperation.Companion.checkIfRequiresPublicKey
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.ALIAS_ALREADY_TAKEN_ERROR
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.KEY_CANNOT_PERFORM_OPERATION_ERROR
import com.tecknobit.kassaforte.services.helpers.KassaforteServiceImplManager
import com.tecknobit.kassaforte.services.helpers.KassaforteServiceImplManager.Companion.encode64
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import kotlin.io.encoding.Base64


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class KassaforteAsymmetricServiceImpl actual constructor() : KassaforteServiceImpl() {

    private val serviceImplManager = KassaforteServiceImplManager.getInstance(
        serializer = KeyInfo.serializer()
    )

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
                algorithm = algorithm,
                keyPurposes = purposes,
                keyPair = keyPair
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
        return if (keyOperation.checkIfRequiresPublicKey())
            keyInfo.resolvePublicKey()
        else
            keyInfo.resolvePrivateKey()
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
        val publicKey: String,
    ) : KeyDetailsSheet<String> {

        @Transient
        private val keyFactory = KeyFactory.getInstance(algorithm)

        constructor(
            algorithm: String,
            keyPurposes: KeyPurposes,
            keyPair: KeyPair,
        ) : this(
            algorithm = algorithm,
            keyPurposes = keyPurposes,
            key = keyPair.private.encode64(),
            publicKey = keyPair.public.encode64()
        )

        @Returner
        fun resolvePrivateKey(): PrivateKey {
            val decodedKey = decodeKey(
                encodedKey = key
            )
            return keyFactory.generatePrivate(PKCS8EncodedKeySpec(decodedKey))
        }

        @Returner
        fun resolvePublicKey(): PublicKey {
            val decodedKey = decodeKey(
                encodedKey = publicKey
            )
            return keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))
        }

        @Returner
        private fun decodeKey(
            encodedKey: String,
        ): ByteArray {
            return Base64.decode(encodedKey)
        }

    }

}