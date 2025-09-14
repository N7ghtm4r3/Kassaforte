package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.KeyPurposes
import com.tecknobit.kassaforte.key.genspec.BlockModeType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteSymmetricService : KassaforteKeysService<SymmetricKeyGenSpec>() {

    actual override fun generateKey(
        alias: String,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        val subtleCrypto = subtleCrypto()
        val genSpec = resolveKeyGenSpec(
            algorithm = keyGenSpec.algorithm.value,
            blockType = keyGenSpec.blockMode.value,
            size = keyGenSpec.keySize ?: 128
        )
        val keyUsages = resolveUsages(
            purposes = purposes
        )
        val key = subtleCrypto.generateKey(
            algorithm = genSpec,
            extractable = true,
            keyUsages = keyUsages
        )
    }

    @Assembler
    private fun resolveUsages(
        purposes: KeyPurposes,
    ): JsArray<JsString> {
        val keyUsages = mutableListOf<String>()
        if (purposes.canEncrypt)
            keyUsages.add("encrypt")
        if (purposes.canDecrypt)
            keyUsages.add("decrypt")
        if (purposes.canSign)
            keyUsages.add("sign")
        if (purposes.canVerify)
            keyUsages.add("verify")
        if (purposes.canWrapKey)
            keyUsages.add("wrapKey")
        if (purposes.canAgree)
            keyUsages.add("deriveKey")
        if (keyUsages.isEmpty())
            throw IllegalStateException("Key usages not valid")
        return keyUsages.map { it.toJsString() }.toJsArray()
    }

    actual override fun aliasExists(
        alias: String
    ): Boolean {
        TODO("Not yet implemented")
    }

    actual fun encrypt(
        alias: String,
        blockModeType: BlockModeType?,
        paddingType: EncryptionPaddingType?,
        data: Any,
    ): String {
        TODO("Not yet implemented")
    }

    actual fun decrypt(
        alias: String,
        blockModeType: BlockModeType?,
        paddingType: EncryptionPaddingType?,
        data: String,
    ): String {
        TODO("Not yet implemented")
    }

    actual override fun deleteKey(
        alias: String
    ) {
        TODO("Not yet implemented")
    }

}

@JsFun("() => window.crypto.subtle")
private external fun subtleCrypto(): SubtleCrypto

@JsFun(
    """
    (algorithm, blockType, size) => ({
        name: `${'$'}{algorithm}-${'$'}{blockType}`,
        length: size
    })
    """
)
@Returner
private external fun resolveKeyGenSpec(
    algorithm: String,
    blockType: String,
    size: Int,
): KeyGenSpec