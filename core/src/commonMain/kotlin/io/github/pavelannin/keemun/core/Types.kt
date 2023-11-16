package io.github.pavelannin.keemun.core

import kotlinx.coroutines.CoroutineScope

/** Effect that will return the necessary dependencies to initialize the state.. */
fun interface PreInitEffect<Deps> {
    suspend operator fun invoke(): Deps
}

/** Returns default state and effects by previous state and dependencies. */
fun interface Init<State, Effect, Deps> {
    operator fun invoke(previous: State?, deps: Deps): Pair<State, Set<Effect>>
}

/** Creates a next state and side-effects from a message and current state. */
fun interface Update<State, in Msg, Effect> {
    operator fun invoke(msg: Msg, state: State): Pair<State, Set<Effect>>
}

/** Dispatches a message to the runtime. */
fun interface Dispatch<in Msg> {
    suspend operator fun invoke(msg: Msg)
}

/** Handling `effect` and `dispatch` messages. */
fun interface EffectHandler<Effect, Msg> {
    suspend operator fun CoroutineScope.invoke(effect: Effect, dispatch: Dispatch<Msg>)
}

/** Transformation [T] from the feature state. */
fun interface StateTransform<State, T> {
    suspend operator fun invoke(state: State): T
}
