package io.github.pavelannin.keemun.swiftui.interop

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/** Wrapper for Kotlin suspend functions to be controlled from Swift. */
class KotlinNativeSuspend<Result : Any>(private val suspendFunction: suspend () -> Result) {

    /**
     * Executes a Kotlin suspend function.
     *
     * @param coroutineScope The scope in which the Kotlin suspend function will be launched.
     * @param result The callback with the result of executing the Kotlin suspend function.
     * @param error The callback notifying of an exception during the execution of the Kotlin suspend function.
     * @param cancelled The callback notifying of the cancellation of the Kotlin suspend function. *
     *
     * @see KotlinNativeCancellable
     */
    fun run(
        coroutineScope: CoroutineScope,
        result: (Result) -> Unit,
        error: (KotlinNativeError) -> Unit,
        cancelled: (KotlinNativeError) -> Unit,
    ): KotlinNativeCancellable {
        val job = coroutineScope.launch {
            try {
                result(suspendFunction())
            } catch (error: Throwable) {
                error(error.asKotlinNativeError())
            }
        }
        job.invokeOnCompletion { cause ->
            if (cause is CancellationException) cancelled(cause.asKotlinNativeError())
        }
        return job.asKotlinNativeCancellable()
    }
}
