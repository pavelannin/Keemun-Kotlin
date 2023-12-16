package io.github.pavelannin.keemun.swiftui.interop

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/** Wrapper for [StateFlow] to be controlled from Swift. */
class KotlinNativeStateFlow<T : Any>(private val stateFlow: StateFlow<T>) {
    val value: T get() = stateFlow.value

    /** Subscribes to receive changes from a [StateFlow].
     * @param coroutineScope The scope in which the subscription will be launched.
     * @param collector The callback invoked for each new value.
     * @param complete The callback invoked when the [StateFlow] is completed. If null is passed, the [StateFlow] completed successfully,
     * otherwise with an error.
     * @param cancelled The callback invoked when the subscription is cancelled.
     *
     * @see KotlinNativeCancellable
     */
    fun collect(
        coroutineScope: CoroutineScope,
        collector: (T) -> Unit,
        complete: (KotlinNativeError?) -> Unit,
        cancelled: (KotlinNativeError) -> Unit,
    ): KotlinNativeCancellable {
        val job = coroutineScope.launch {
            try {
                stateFlow.collect { value -> collector(value) }
                complete(null)
            }  catch (error: Throwable) {
                complete(error.asKotlinNativeError())
            }
        }
        job.invokeOnCompletion { cause -> if (cause is CancellationException) cancelled(cause.asKotlinNativeError()) }
        return job.asKotlinNativeCancellable()
    }
}

/** Converts a [StateFlow] to a [KotlinNativeStateFlow]. */
@HiddenFromObjC
fun <T : Any> StateFlow<T>.asKotlinNativeStateFlow() = KotlinNativeStateFlow(stateFlow = this)
