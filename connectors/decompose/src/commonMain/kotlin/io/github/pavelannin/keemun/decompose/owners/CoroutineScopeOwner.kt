package io.github.pavelannin.keemun.decompose.owners

import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

/**
 * Creates a [CoroutineScopeOwner] with a binding to the [LifecycleOwner]'s lifecycle.
 *
 * @param job Job cancelled when the lifecycle is destroyed. Stays a [SupervisorJob] unless the caller passes another one,
 * so a [kotlinx.coroutines.CoroutineExceptionHandler] from [coroutineContext] receives uncaught child exceptions.
 * @param dispatcher Dispatcher of the created scope.
 * @param coroutineContext Additional context merged into the scope. [job] and [dispatcher] override the same elements
 * from this context. A [kotlinx.coroutines.CoroutineExceptionHandler] placed here is preserved.
 *
 * @see CoroutineScopeOwner
 */
fun LifecycleOwner.CoroutineScopeOwner(
    job: Job = SupervisorJob(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
): CoroutineScopeOwner {
    lifecycle.doOnDestroy(job::cancel)
    return object : CoroutineScopeOwner {
        override val coroutineScope: CoroutineScope = CoroutineScope(context = coroutineContext + dispatcher + job)
    }
}
/** Represents a holder of [CoroutineScope]. */
interface CoroutineScopeOwner {
    val coroutineScope: CoroutineScope
}
