package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.kassaforte.enums.ExportFormat.PKCS8
import com.tecknobit.kassaforte.enums.ExportFormat.SPKI
import com.tecknobit.kassaforte.helpers.IndexedDBManager
import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKeyPair
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.KeyGenSpec
import com.tecknobit.kassaforte.wrappers.crypto.key.raw.RawCryptoKeyPair
import kotlinx.coroutines.launch

/**
 * The `KassaforteAsymmetricServiceManager` class allows to perform indexedb's operations on asymmetric keys
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteServiceManager
 * @see KassaforteServiceImplManager
 * @see CryptoKeyPair
 * @see RawCryptoKeyPair
 */
internal class KassaforteAsymmetricServiceManager : KassaforteServiceImplManager<CryptoKeyPair, RawCryptoKeyPair>() {

    /**
     * Method used to secure store a new generated asymmetric key pair
     *
     * @param alias The alias used to identify the key
     * @param algorithm The gen spec of the key to generate
     * @param generatedKey The newly generated key
     */
    override fun store(
        alias: String,
        algorithm: KeyGenSpec,
        generatedKey: CryptoKeyPair,
    ) {
        managerScope.launch {
            val privateKey = exportKey(
                key = generatedKey.privateKey,
                format = PKCS8
            )
            val publicKey = exportKey(
                key = generatedKey.publicKey,
                format = SPKI
            )
            IndexedDBManager.addKeyPair(
                alias = alias,
                algorithm = algorithm,
                keyPair = generatedKey,
                privateKey = privateKey,
                publicKey = publicKey
            )
        }
    }

}