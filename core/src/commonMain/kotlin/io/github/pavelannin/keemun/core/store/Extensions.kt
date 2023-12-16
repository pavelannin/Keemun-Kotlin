package io.github.pavelannin.keemun.core.store

import io.github.pavelannin.keemun.core.StateTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import kotlin.native.HiddenFromObjC

/**
 * Transforms [Store<InState, InMsg>] into [Store<OutState, OutMsg>], wrapping [Store<InState, InMsg>] with the implementation of
 * [Store<OutState, OutMsg>].
 *
 * @param stateTransform - Function that transforms [InState] into [OutState].
 * @param messageTransform - Function that transforms [OutMsg] into [InMsg].
 *
 * @see StateTransform
*/
@OptIn(ExperimentalCoroutinesApi::class)
@HiddenFromObjC
inline fun <InState : Any, InMsg : Any, OutState : Any, OutMsg : Any> Store<InState, InMsg>.transform(
    stateTransform: StateTransform<InState, OutState>,
    crossinline messageTransform: (OutMsg) -> InMsg,
): Store<OutState, OutMsg> {
    val store = this
    return object : Store<OutState, OutMsg> {
        override val scope: CoroutineScope get() = store.scope
        override val state: StateFlow<OutState> get() = store.state
            .mapLatest(stateTransform::invoke)
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = store.scope,
                started = SharingStarted.Lazily,
                initialValue = runBlocking { stateTransform(store.state.value) },
            )

        override fun dispatch(msg: OutMsg) = store dispatch messageTransform(msg)
        override suspend fun syncDispatch(msg: OutMsg) = store syncDispatch messageTransform(msg)
    }
}

/**
 * Transforms [Store<InState, InMsg>] into [Store<OutState, OutMsg>], wrapping [Store<InState, InMsg>] with the implementation of
 * [Store<OutState, OutMsg>]. [OutMsg] implements [InMsg] and is converted through type slicing.
 *
 * @param stateTransform - Function that transforms [InState] into [OutState].
 *
 * @see StateTransform
 */
inline fun <InState : Any, InMsg : Any, OutState : Any, OutMsg : InMsg> Store<InState, InMsg>.transform(
    stateTransform: StateTransform<InState, OutState>,
): Store<OutState, OutMsg> = transform(
    stateTransform = stateTransform,
    messageTransform = { it },
)

/**
 * Transforms [Store<State, InMsg>] into [Store<State, OutMsg>], wrapping [Store<State, InMsg>] with the implementation of
 * [Store<State, OutMsg>]. [OutMsg] implements [InMsg] and is converted through type slicing.
 *
 * @see StateTransform
 */
inline fun <State : Any, InMsg : Any, OutMsg : InMsg> Store<State, InMsg>.transform(): Store<State, OutMsg> = transform(
    stateTransform = { it },
    messageTransform = { it },
)
