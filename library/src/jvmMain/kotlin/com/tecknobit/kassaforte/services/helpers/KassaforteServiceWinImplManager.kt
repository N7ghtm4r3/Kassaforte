@file:OptIn(ExperimentalStdlibApi::class)

package com.tecknobit.kassaforte.services.helpers

import com.github.windpapi4j.WinDPAPI
import com.github.windpapi4j.WinDPAPI.CryptProtectFlag.CRYPTPROTECT_UI_FORBIDDEN
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.equinoxcore.util.getApplicationLocalDataDirectoryPath
import com.tecknobit.kassaforte.Kassaforte
import com.tecknobit.kassaforte.key.usages.KeyDetailsSheet
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.util.*
import kotlin.io.encoding.Base64

internal class KassaforteServiceWinImplManager<KI : KeyDetailsSheet<*>>(
    serializer: KSerializer<KI>,
) : KassaforteServiceImplManager<KI>(
    serializer = serializer
) {

    private var winDPAPI = WinDPAPI.newInstance(CRYPTPROTECT_UI_FORBIDDEN)

    override fun storeKeyData(
        alias: String,
        keyInfo: KI,
    ) {
        val encodedKeyData = formatKeyData(
            keyInfo = keyInfo,
            encode64 = false
        )
        val encryptedData = winDPAPI.protectData(encodedKeyData.encodeToByteArray())
        AliasManager.registry(
            alias = alias,
            data = encryptedData
        )
    }

    override fun isAliasTaken(
        alias: String,
    ): Boolean {
        return AliasManager.isAliasRegistered(
            alias = alias
        )
    }

    override fun retrieveKey(
        alias: String,
    ): KI {
        val registryData = AliasManager.readAliasRegistry(
            alias = alias
        )
        val decryptedRegistryData = winDPAPI.unprotectData(registryData)
        val keyInfo: KI = Json.decodeFromString(
            deserializer = serializer,
            string = decryptedRegistryData.decodeToString()
        )
        return keyInfo
    }

    override fun removeKey(
        alias: String,
    ) {
        AliasManager.remove(
            alias = alias
        )
    }

    private object AliasManager {

        private val ALIASES_BUCKET = "${getApplicationLocalDataDirectoryPath()}\\.kassaforte"

        init {
            createBucketDirectory()
        }

        private fun createBucketDirectory() {
            val bucketDirectory = File(ALIASES_BUCKET)
            bucketDirectory.mkdir()
        }

        fun registry(
            alias: String,
            data: ByteArray,
        ) {
            val registryPathName = obtainAliasPathName()
            saveDataInRegistry(
                registryPathName = registryPathName,
                data = data
            )
            val kassaforte = Kassaforte(alias)
            kassaforte.safeguard(
                key = alias,
                data = registryPathName
            )
        }

        @Returner
        private fun obtainAliasPathName(): String {
            val aliasUuid = UUID.randomUUID().toString().replace("-", "")
            return ALIASES_BUCKET + "\\$aliasUuid"
        }

        private fun saveDataInRegistry(
            registryPathName: String,
            data: ByteArray,
        ) {
            val encodedData = Base64.encode(data)
            val fileWriter = FileWriter(registryPathName)
            fileWriter.use { writer ->
                writer.write(encodedData)
            }
        }

        fun isAliasRegistered(
            alias: String,
        ): Boolean {
            return retrieveAliasRegistryPath(
                alias = alias
            ) != null
        }

        fun readAliasRegistry(
            alias: String,
        ): ByteArray {
            val aliasRegistry = retrieveAliasRegistry(
                alias = alias
            )
            val registryData = Files.readAllBytes(aliasRegistry.toPath())
            return Base64.decode(registryData)
        }

        private fun retrieveAliasRegistryPath(
            alias: String,
        ): String? {
            val kassaforte = Kassaforte(alias)
            val aliasRegistryPath = kassaforte.unsuspendedWithdraw(
                key = alias
            )
            return aliasRegistryPath
        }

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

        private fun deleteRegistryFile(
            alias: String,
        ): Boolean {
            val aliasRegistry = retrieveAliasRegistry(
                alias = alias
            )
            return aliasRegistry.delete()
        }

        private fun retrieveAliasRegistry(
            alias: String,
        ): File {
            val aliasRegistryPath = retrieveAliasRegistryPath(
                alias = alias
            )
            if (aliasRegistryPath == null)
                throw RuntimeException(IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR)
            return File(aliasRegistryPath)
        }

    }

}
