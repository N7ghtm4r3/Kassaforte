@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.utils

import kotlinx.coroutines.await
import kotlin.js.Promise

actual suspend fun <T : JsAny?> Promise<T>.await(): T {
    return this.await()
}