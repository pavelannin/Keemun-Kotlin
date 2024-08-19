package io.github.pavelannin.keemun.decompose.owners

import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

/**
 * Creates a [CoroutineScopeOwner] with a binding to the [LifecycleOwner]'s lifecycle.
 *
 * @see CoroutineScopeOwner
 */
fun LifecycleOwner.CoroutineScopeOwner(
    job: Job = SupervisorJob(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): CoroutineScopeOwner {
    lifecycle.doOnDestroy(job::cancel)
    return object : CoroutineScopeOwner {
        override val coroutineScope: CoroutineScope = CoroutineScope(context = dispatcher + job)
    }
}
/** Represents a holder of [CoroutineScope]. */
interface CoroutineScopeOwner {
    val coroutineScope: CoroutineScope
}
