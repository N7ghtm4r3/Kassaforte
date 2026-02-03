@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.utils

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.Promise

expect suspend fun <T : JsAny?> Promise<T>.await(): T