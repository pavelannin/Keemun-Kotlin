package io.github.pavelannin.keemun.swiftui.interop

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob

/** Kotlin coroutine scopes. */
object KotlinNativeCoroutineScope {
    val default: CoroutineScope get() = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val main: CoroutineScope get() = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    val io: CoroutineScope get() = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val unconfined: CoroutineScope get() = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
}
