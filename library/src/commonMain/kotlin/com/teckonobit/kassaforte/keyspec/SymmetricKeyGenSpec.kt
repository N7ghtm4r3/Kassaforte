package com.teckonobit.kassaforte.keyspec

data class SymmetricKeyGenSpec(
    override val algorithm: AlgorithmType,
    override val keySize: Int? = null,
    override val purposes: KeyPurposes,
    val blockModes: Array<BlockModeType> = emptyArray(),
    override val digests: Array<DigestType> = emptyArray(),
    override val encryptionPaddings: Array<EncryptionPaddingType> = emptyArray()
): KassaforteKeyGenSpec {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SymmetricKeyGenSpec) return false

        if (keySize != other.keySize) return false
        if (algorithm != other.algorithm) return false
        if (purposes != other.purposes) return false
        if (!blockModes.contentEquals(other.blockModes)) return false
        if (!digests.contentEquals(other.digests)) return false
        if (!encryptionPaddings.contentEquals(other.encryptionPaddings)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = keySize ?: 0
        result = 31 * result + algorithm.hashCode()
        result = 31 * result + purposes.hashCode()
        result = 31 * result + blockModes.contentHashCode()
        result = 31 * result + digests.contentHashCode()
        result = 31 * result + encryptionPaddings.contentHashCode()
        return result
    }

}

