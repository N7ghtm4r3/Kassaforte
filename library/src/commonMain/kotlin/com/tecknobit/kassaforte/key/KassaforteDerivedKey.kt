package com.tecknobit.kassaforte.key

import com.tecknobit.kassaforte.key.genspec.Digest
import com.tecknobit.kassaforte.key.genspec.KeySize
import kotlinx.serialization.Serializable

/**
 * The `KassaforteDerivedKey` data class represents a derived key information
 *
 * @property key The derived key content, encoded in `Base64` format
 * @property salt The salt used during the key derivation
 * @property iterationCount The number of iteration used to derive the key
 * @property keySize The size of the derived key
 * @property digest The digest used to derive the key
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @since Revision Three
 */
@Serializable
data class KassaforteDerivedKey(
    val key: String,
    val salt: ByteArray,
    val iterationCount: Int,
    val keySize: KeySize,
    val digest: Digest,
) {

    companion object {

        /**
         * `DEFAULT_ITERATION_COUNT` the minimum number of iterations considered secure for key derivation
         */
        const val DEFAULT_ITERATION_COUNT: Int = 600_000

    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * Implementations must fulfil the following requirements:
     * * Reflexive: for any non-null value `x`, `x.equals(x)` should return true.
     * * Symmetric: for any non-null values `x` and `y`, `x.equals(y)` should return true if and only if `y.equals(x)` returns true.
     * * Transitive: for any non-null values `x`, `y`, and `z`, if `x.equals(y)` returns true and `y.equals(z)` returns true, then `x.equals(z)` should return true.
     * * Consistent: for any non-null values `x` and `y`, multiple invocations of `x.equals(y)` consistently return true or consistently return false, provided no information used in `equals` comparisons on the objects is modified.
     * * Never equal to null: for any non-null value `x`, `x.equals(null)` should return false.
     *
     * Read more about [equality](https://kotlinlang.org/docs/reference/equality.html) in Kotlin.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KassaforteDerivedKey) return false

        if (!salt.contentEquals(other.salt)) return false
        if (keySize != other.keySize) return false

        return true
    }

    /**
     * Returns a hash code value for the object.
     *
     * The general contract of `hashCode` is:
     * * Whenever it is invoked on the same object more than once, the `hashCode` method must consistently return the same integer, provided no information used in `equals` comparisons on the object is modified.
     * * If two objects are equal according to the `equals()` method, then calling the `hashCode` method on each of the two objects must produce the same integer result.
     */
    override fun hashCode(): Int {
        var result = salt.contentHashCode()
        result = 31 * result + keySize.hashCode()
        return result
    }
}
