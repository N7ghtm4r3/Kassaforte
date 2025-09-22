package com.tecknobit.kassaforte.services.helpers

/**
 * The `KassaforteServiceImplManager` class allows to perform operations that [KassaforteAsymmetricServiceManager]
 * and [KassaforteSymmetricServiceManager] have in common
 *
 * It is particularly useful to avoid to break the `expect/actual` implementation and clean implement shared code avoiding
 * duplication
 *
 * @param K The type of the key the managers handles
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteServiceManager
 */
// TODO: TO ANNOTATE WITH @Structure
internal abstract class KassaforteServiceImplManager<K> : KassaforteServiceManager<K>