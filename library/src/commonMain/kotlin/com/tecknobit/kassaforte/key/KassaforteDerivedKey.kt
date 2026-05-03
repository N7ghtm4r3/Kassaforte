package com.tecknobit.kassaforte.key

import com.tecknobit.kassaforte.key.genspec.Digest
import com.tecknobit.kassaforte.key.genspec.KeySize
import kotlinx.serialization.Serializable

// TODO: TO DOCU SINCE
@Serializable
data class KassaforteDerivedKey(
    val key: String,
    val salt: ByteArray,
    val iterationCount: Int,
    val keySize: KeySize,
    val digest: Digest,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KassaforteDerivedKey) return false

        if (!salt.contentEquals(other.salt)) return false
        if (keySize != other.keySize) return false

        return true
    }

    override fun hashCode(): Int {
        var result = salt.contentHashCode()
        result = 31 * result + keySize.hashCode()
        return result
    }
}
