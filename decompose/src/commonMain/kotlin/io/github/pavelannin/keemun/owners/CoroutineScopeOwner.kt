package io.github.pavelannin.keemun.owners

import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

fun LifecycleOwner.coroutineScope(): CoroutineScope {
    val job = SupervisorJob()
    lifecycle.doOnDestroy(job::cancel)
    return CoroutineScope(context = Dispatchers.Default + job)
}
