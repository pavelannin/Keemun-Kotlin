package io.github.pavelannin.keemun.core

import kotlinx.coroutines.CoroutineScope
import kotlin.native.HiddenFromObjC

/** Effect that will return the necessary dependencies to initialize the state. */
@HiddenFromObjC
fun interface PreInitEffect<out Deps> {
    suspend operator fun invoke(): Deps
}

/** Returns default state and effects by previous state and dependencies. */
@HiddenFromObjC
fun interface Init<State, out Effect, in Deps> {
    operator fun invoke(previous: State?, deps: Deps): Pair<State, Set<Effect>>
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
