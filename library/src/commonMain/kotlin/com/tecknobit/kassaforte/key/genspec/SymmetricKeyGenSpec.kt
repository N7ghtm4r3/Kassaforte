package com.tecknobit.kassaforte.key.genspec

data class SymmetricKeyGenSpec(
    override val algorithm: AlgorithmType,
    override val keySize: Int? = null,
    val blockMode: BlockModeType,
    // TODO: CHECK WHETHER KEEP ARRAY
    override val digests: Array<DigestType> = emptyArray(),
    override val encryptionPaddings: Array<EncryptionPaddingType> = emptyArray(),
): KassaforteKeyGenSpec {

    constructor(
        algorithm: AlgorithmType,
        keySize: Int? = null,
        blockMode: BlockModeType,
        digest: DigestType? = null,
        encryptionPadding: EncryptionPaddingType? = null,
    ): this (
        algorithm = algorithm,
        keySize = keySize,
        blockMode = blockMode,
        digests = if(digest != null)
            arrayOf(digest)
        else
            emptyArray(),
        encryptionPaddings = if(encryptionPadding != null)
            arrayOf(encryptionPadding)
        else
            emptyArray()
    )

    // TODO: CHECK TO KEEP
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SymmetricKeyGenSpec) return false

        if (keySize != other.keySize) return false
        if (algorithm != other.algorithm) return false
        if (blockMode != other.blockMode) return false
        if (!digests.contentEquals(other.digests)) return false
        if (!encryptionPaddings.contentEquals(other.encryptionPaddings)) return false

        return true
    }

    // TODO: CHECK TO KEEP
    override fun hashCode(): Int {
        var result = keySize ?: 0
        result = 31 * result + algorithm.hashCode()
        result = 31 * result + blockMode.hashCode()
        result = 31 * result + digests.contentHashCode()
        result = 31 * result + encryptionPaddings.contentHashCode()
        return result
    }


}

