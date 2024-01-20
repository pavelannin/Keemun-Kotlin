package io.github.pavelannin.keemun.core

import kotlinx.coroutines.CoroutineScope
import kotlin.native.HiddenFromObjC

/** Returns default state and effects by previous state. */
@HiddenFromObjC
fun interface Start<State, out Effect> {
    operator fun invoke(savedState: State?): Pair<State, Set<Effect>>
}

/** Creates a next state and side-effects from a message and current state. */
@HiddenFromObjC
fun interface Update<State, in Msg, out Effect> {
    operator fun invoke(msg: Msg, state: State): Pair<State, Set<Effect>>
}

/** Dispatches a message to the runtime. */
@HiddenFromObjC
fun interface Dispatch<in Msg> {
    suspend operator fun invoke(msg: Msg)
}

/** Handling `effect` and `dispatch` messages. */
@HiddenFromObjC
fun interface EffectHandler<in Effect, out Msg> {
    suspend operator fun CoroutineScope.invoke(effect: Effect, dispatch: Dispatch<Msg>)
}

/** Transformation [T] from the feature state. */
@HiddenFromObjC
fun interface StateTransform<in State, out T> {
    suspend operator fun invoke(state: State): T
}
