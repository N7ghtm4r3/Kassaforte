@file:OptIn(ExperimentalStdlibApi::class)

package com.tecknobit.kassaforte.services.helpers

import com.github.windpapi4j.WinDPAPI
import com.github.windpapi4j.WinDPAPI.CryptProtectFlag.CRYPTPROTECT_UI_FORBIDDEN
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.equinoxcore.util.getApplicationLocalDataDirectoryPath
import com.tecknobit.kassaforte.Kassaforte
import com.tecknobit.kassaforte.key.usages.KeyDetailsSheet
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR
import com.tecknobit.kassaforte.util.decode
import com.tecknobit.kassaforte.util.encode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.util.*
import kotlin.io.encoding.Base64

/**
 * The `KassaforteServiceGenOsImplManager` class allows to perform operations that [com.tecknobit.kassaforte.services.impls.KassaforteSymmetricServiceImpl]
 * and [com.tecknobit.kassaforte.services.impls.KassaforteAsymmetricServiceImpl] have in common
 *
 * It is particularly useful to avoid to break the `expect/actual` implementation and clean implement shared code avoiding
 * duplication.
 *
 * This particular service manager is adopted when the OS where the application is running on is `Windows`. The workflow
 * is different from other operating systems because on [Windows Credentials](https://learn.microsoft.com/en-us/windows/win32/secauthn/credentials-management)
 * are stored the aliases and the related paths of the files where is stored the generated key or keypair.
 * The data are encrypted and decrypted by the [windpapi4j](https://github.com/peter-gergely-horvath/windpapi4j) library which provides the [DPAPI](https://learn.microsoft.com/en-us/dotnet/standard/security/how-to-use-data-protection)
 * protection
 *
 * @property serializer The serializer to use to correctly serialize a key info object from the stored data
 *
 * @param KI The type of the key info used by the service
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteServiceManager
 * @see KassaforteServiceImplManager
 */
internal class KassaforteServiceWinImplManager<KI : KeyDetailsSheet<*>>(
    serializer: KSerializer<KI>,
) : KassaforteServiceImplManager<KI>(
    serializer = serializer
) {

    /**
     * `winDPAPI` the instance used to perform the `DPAPI` protection on the keys
     */
    private var winDPAPI = WinDPAPI.newInstance(CRYPTPROTECT_UI_FORBIDDEN)

    /**
     * Method used to store the data of the generated key
     *
     * @param alias The alias which identify the key
     * @param keyInfo The extra information of the generated key to store
     */
    override fun storeKeyData(
        alias: String,
        keyInfo: KI,
    ) {
        val encodedKeyData = formatKeyData(
            keyInfo = keyInfo,
            encode64 = false
        )
        val encryptedData = winDPAPI.protectData(encodedKeyData.encodeToByteArray())
        KeysManager.registry(
            alias = alias,
            data = encryptedData
        )
    }

    /**
     * Method used to check whether the alias has been already taken to identify other key
     *
     * @param alias The alias to check
     *
     * @return whether the alias has been already taken as [Boolean]
     */
    override fun isAliasTaken(
        alias: String,
    ): Boolean {
        return KeysManager.isAliasRegistered(
            alias = alias
        )
    }

    /**
     * Method used to retrieve from the secure storage the specified key
     *
     * @param alias The alias used to store the key
     *
     * @return the key as [KI]
     */
    override fun retrieveKey(
        alias: String,
    ): KI {
        val registryData = KeysManager.readRegistry(
            alias = alias
        )
        val decryptedRegistryData = winDPAPI.unprotectData(registryData)
        val keyInfo: KI = Json.decodeFromString(
            deserializer = serializer,
            string = decryptedRegistryData.decodeToString()
        )
        return keyInfo
    }

    /**
     * Method used to remove from the secure store the specified key
     *
     * @param alias The alias of the key to remove
     */
    override fun removeKey(
        alias: String,
    ) {
        KeysManager.remove(
            alias = alias
        )
    }

    /**
     * The `KeysManager` object provides the physical management of the keys such as their storage, deletion of the
     * keys and the related registry files
     *
     * @author Tecknobit - N7ghtm4r3
     */
    private object KeysManager {

        /**
         * `KEYS_BUCKET` the bucket folder where the registries of the keys will be located
         */
        private val KEYS_BUCKET = "${getApplicationLocalDataDirectoryPath()}\\.kassaforte"

        init {
            createBucketFolder()
        }

        /**
         * Method used to create, if not already exists, the bucket folder
         */
        private fun createBucketFolder() {
            val bucketDirectory = File(KEYS_BUCKET)
            bucketDirectory.mkdir()
        }

        /**
         * Method used to registry a new generated key into [KEYS_BUCKET] folder
         *
         * @param alias The alias which identify the key
         * @param data The encrypted data of the key
         */
        fun registry(
            alias: String,
            data: ByteArray,
        ) {
            val registryPathName = obtainRegistryPath()
            saveDataInRegistry(
                registryPath = registryPathName,
                data = data
            )
            val kassaforte = Kassaforte(alias)
            kassaforte.safeguard(
                key = alias,
                data = registryPathName
            )
        }

        /**
         * Method used to obtain a unique path for the new registry
         *
         * @return the unique path as [String]
         */
        @Returner
        private fun obtainRegistryPath(): String {
            val aliasUuid = UUID.randomUUID().toString().replace("-", "")
            return KEYS_BUCKET + "\\$aliasUuid"
        }

        /**
         * Method used to write into the registry the encrypted [data] of the key.
         *
         * The data will be formatted in [Base64] format
         *
         * @param registryPath The path of the registry
         * @param data The encrypted data of the key to write
         */
        private fun saveDataInRegistry(
            registryPath: String,
            data: ByteArray,
        ) {
            val encodedData = encode(data)
            val registryWriter = FileWriter(registryPath)
            registryWriter.use { writer ->
                writer.write(encodedData)
            }
        }

        /**
         * Method used to check whether the alias is already assigned to other key
         *
         * @param alias The alias to check
         *
         * @return whether the alias is available as [Boolean]
         */
        fun isAliasRegistered(
            alias: String,
        ): Boolean {
            return retrieveRegistryPath(
                alias = alias
            ) != null
        }

        /**
         * Method used to read the data from a registry
         *
         * @param alias The alias which identify the key
         *
         * @return the encrypted data from the registry as [ByteArray]
         */
        fun readRegistry(
            alias: String,
        ): ByteArray {
            val keyRegistry = retrieveRegistry(
                alias = alias
            )
            val registryData = Files.readAllBytes(keyRegistry.toPath())
            return decode(registryData)
        }

        /**
         * Method used to retrieve from the [Kassaforte] the path of the registry specified by the [alias]
         *
         * @param alias The alias which identify the key
         *
         * @return the path of the registry as [String] when exists, `null` otherwise
         */
        private fun retrieveRegistryPath(
            alias: String,
        ): String? {
            val kassaforte = Kassaforte(alias)
            val aliasRegistryPath = kassaforte.unsuspendedWithdraw(
                key = alias
            )
            return aliasRegistryPath
        }

        /**
         * Method used to physically delete the registry, and the data from the [Kassaforte],
         * of the key specified by the [alias]
         *
         * @param alias The alias which identify the key
         */
        fun remove(
            alias: String,
        ) {
            val registryDeleted = deleteRegistryFile(
                alias = alias
            )
            if (!registryDeleted)
                throw RuntimeException("Cannot delete $alias entry")
            val kassaforte = Kassaforte(alias)
            kassaforte.remove(
                key = alias
            )
        }

        /**
         * Method used to physically delete the registry specified by the [alias]
         *
         * @param alias The alias which identify the key
         *
         * @return the result of the deletion as [Boolean]
         */
        private fun deleteRegistryFile(
            alias: String,
        ): Boolean {
            val aliasRegistry = retrieveRegistry(
                alias = alias
            )
            return aliasRegistry.delete()
        }

        /**
         * Method used to retrieve the registry file
         *
         * @param alias The alias which identify the key
         *
         * @return the registry as [File]
         */
        private fun retrieveRegistry(
            alias: String,
        ): File {
            val aliasRegistryPath = retrieveRegistryPath(
                alias = alias
            )
            if (aliasRegistryPath == null)
                throw RuntimeException(IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR)
            return File(aliasRegistryPath)
        }

    }

}
