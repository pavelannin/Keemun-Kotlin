package io.github.pavelannin.keemun.swiftui.interop

import kotlinx.coroutines.Job

/** Wrapper for [Job] to be controlled from Swift. */
class KotlinNativeCancellable(private val job: Job) {

    /** Cancels the execution of the operation. */
    fun cancel() { job.cancel() }
}

/** Converts a [Job] to a [KotlinNativeCancellable]. */
@HiddenFromObjC
fun Job.asKotlinNativeCancellable() = KotlinNativeCancellable(job = this)
