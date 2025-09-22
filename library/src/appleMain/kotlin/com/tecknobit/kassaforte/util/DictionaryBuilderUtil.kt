@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ptr
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks

/**
 * Method used to assemble a native [CFMutableDictionaryRef] object
 *
 * @param capacity The capacity of the dictionary, how many entries must have
 * @param addEntries Callback to add entries into the assembling dictionary
 *
 * @return the native dictionary object as [CFMutableDictionaryRef]
 */
// TODO TO ANNOTATE WITH @Assembler
fun kassaforteDictionary(
    capacity: Long,
    addEntries: CFMutableDictionaryRef.() -> Unit,
): CFMutableDictionaryRef {
    val dictionary = CFDictionaryCreateMutable(
        allocator = null,
        capacity = capacity,
        keyCallBacks = kCFTypeDictionaryKeyCallBacks.ptr,
        valueCallBacks = kCFTypeDictionaryValueCallBacks.ptr
    )!!
    dictionary.addEntries()
    return dictionary
}