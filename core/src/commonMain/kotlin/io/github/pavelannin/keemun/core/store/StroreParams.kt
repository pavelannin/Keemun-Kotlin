package io.github.pavelannin.keemun.core.store

import io.github.pavelannin.keemun.core.EffectHandler
import io.github.pavelannin.keemun.core.Init
import io.github.pavelannin.keemun.core.PreInitEffect
import io.github.pavelannin.keemun.core.Update

/**
 * Required parameters for creating [Store].
 *
 * @see PreInitEffect
 * @see Init
 * @see Update
 * @see EffectHandler
 */
data class StoreParams<State : Any, Msg : Any, Effect : Any, Deps : Any>(
    val preEffectInit: PreInitEffect<Deps>,
    val init: Init<State, Effect, Deps>,
    val update: Update<State, Msg, Effect>,
    val effectHandlers: Set<EffectHandler<Effect, Msg>>,
)

/** @see StoreParams */
inline fun <State : Any, Msg : Any, Effect : Any, Deps : Any> StoreParams(
    preEffectInit: PreInitEffect<Deps>,
    init: Init<State, Effect, Deps>,
    update: Update<State, Msg, Effect>,
    effectHandler: EffectHandler<Effect, Msg>,
) = StoreParams(
    preEffectInit = preEffectInit,
    init = init,
    update = update,
    effectHandlers = setOf(effectHandler)
)

/** @see StoreParams */
inline fun <State : Any, Msg : Any, Effect : Any> StoreParams(
    crossinline init: (previous: State?) -> Pair<State, Set<Effect>>,
    update: Update<State, Msg, Effect>,
    effectHandlers: Set<EffectHandler<Effect, Msg>>,
) = StoreParams(
    preEffectInit = {},
    init = { previous, _ -> init(previous) },
    update = update,
    effectHandlers = effectHandlers
)

/** @see StoreParams */
inline fun <State : Any, Msg : Any, Effect : Any> StoreParams(
    crossinline init: (previous: State?) -> Pair<State, Set<Effect>>,
    update: Update<State, Msg, Effect>,
    effectHandler: EffectHandler<Effect, Msg>,
) = StoreParams(
    preEffectInit = {},
    init = { previous, _ -> init(previous) },
    update = update,
    effectHandlers = setOf(effectHandler),
)
