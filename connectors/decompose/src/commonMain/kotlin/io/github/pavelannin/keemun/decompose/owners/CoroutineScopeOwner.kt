package io.github.pavelannin.keemun.decompose.owners

import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Creates a [CoroutineScopeOwner] with a binding to the [LifecycleOwner]'s lifecycle.
 *
 * @see CoroutineScopeOwner
 */
fun LifecycleOwner.CoroutineScopeOwner(): CoroutineScopeOwner {
    val job = SupervisorJob()
    lifecycle.doOnDestroy(job::cancel)
    return object : CoroutineScopeOwner, CoroutineScope by CoroutineScope(context = Dispatchers.Default + job) {}
}
/** Represents a holder of [CoroutineScope]. */
interface CoroutineScopeOwner : CoroutineScope
