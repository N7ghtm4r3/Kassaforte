package com.tecknobit.kassaforte.adapters

import com.tecknobit.equinoxcore.annotations.RequiresDocumentation
import com.tecknobit.equinoxcore.annotations.Wrapper
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.Digest
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.KassaforteAsymmetricService
import kotlinx.coroutines.runBlocking

@RequiresDocumentation(
    additionalNotes = "TO ADD SINCE"
)
object KassaforteAsymmetricServiceJvm {

    @Wrapper
    @JvmStatic
    fun generateKey(
        algorithm: Algorithm,
        alias: String,
        keyGenSpec: AsymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) = KassaforteAsymmetricService.generateKey(
        algorithm = algorithm,
        alias = alias,
        keyGenSpec = keyGenSpec,
        purposes = purposes
    )

    @Wrapper
    @JvmStatic
    fun encrypt(
        alias: String,
        padding: EncryptionPadding?,
        digest: Digest?,
        data: Any,
    ) = runBlocking {
        KassaforteAsymmetricService.encrypt(
            alias = alias,
            padding = padding,
            digest = digest,
            data = data
        )
    }

    @Wrapper
    @JvmStatic
    fun decrypt(
        alias: String,
        padding: EncryptionPadding?,
        digest: Digest?,
        data: String,
    ) = runBlocking {
        KassaforteAsymmetricService.decrypt(
            alias = alias,
            padding = padding,
            digest = digest,
            data = data
        )
    }

    @Wrapper
    @JvmStatic
    fun deleteKey(
        alias: String,
    ) = KassaforteAsymmetricService.deleteKey(
        alias = alias
    )

}