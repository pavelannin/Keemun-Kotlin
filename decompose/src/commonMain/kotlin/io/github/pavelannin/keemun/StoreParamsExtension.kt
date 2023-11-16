package io.github.pavelannin.keemun

import io.github.pavelannin.keemun.core.EffectHandler
import io.github.pavelannin.keemun.core.Init
import io.github.pavelannin.keemun.core.PreInitEffect
import io.github.pavelannin.keemun.core.Update
import io.github.pavelannin.keemun.core.store.StoreParams

/** @see StoreParams */
inline fun <State : Any, Msg : Any, Effect : Any, Deps : Any, reified ExternalMsg : Msg, reified InternalMsg : Msg> StoreParams(
    preEffectInit: PreInitEffect<Deps>,
    init: Init<State, Effect, Deps>,
    externalUpdate: Update<State, ExternalMsg, Effect>,
    internalUpdate: Update<State, InternalMsg, Effect>,
    effectHandlers: Set<EffectHandler<Effect, InternalMsg>>,
) = StoreParams(
    preEffectInit = preEffectInit,
    init = init,
    update = { msg, state ->
        when (msg) {
            is ExternalMsg -> externalUpdate(msg, state)
            is InternalMsg -> internalUpdate(msg, state)
            else -> error("Unknown type $msg")
        }
    },
    effectHandlers = effectHandlers as Set<EffectHandler<Effect, Msg>>
)

/** @see StoreParams */
inline fun <State : Any, Msg : Any, Effect : Any, Deps : Any, reified ExternalMsg : Msg, reified InternalMsg : Msg> StoreParams(
    preEffectInit: PreInitEffect<Deps>,
    init: Init<State, Effect, Deps>,
    externalUpdate: Update<State, ExternalMsg, Effect>,
    internalUpdate: Update<State, InternalMsg, Effect>,
    effectHandler: EffectHandler<Effect, InternalMsg>,
) = StoreParams(
    preEffectInit = preEffectInit,
    init = init,
    update = { msg, state ->
        when (msg) {
            is ExternalMsg -> externalUpdate(msg, state)
            is InternalMsg -> internalUpdate(msg, state)
            else -> error("Unknown type $msg")
        }
    },
    effectHandler = effectHandler
)

/** @see StoreParams */
@Suppress("UNCHECKED_CAST")
inline fun <State : Any, Msg : Any, Effect : Any, reified ExternalMsg : Msg, reified InternalMsg : Msg> StoreParams(
    crossinline init: (previous: State?) -> Pair<State, Set<Effect>>,
    externalUpdate: Update<State, ExternalMsg, Effect>,
    internalUpdate: Update<State, InternalMsg, Effect>,
    effectHandlers: Set<EffectHandler<Effect, InternalMsg>>,
) = StoreParams(
    init = init,
    update = { msg, state ->
        when (msg) {
            is ExternalMsg -> externalUpdate(msg, state)
            is InternalMsg -> internalUpdate(msg, state)
            else -> error("Unknown type $msg")
        }
    },
    effectHandlers = effectHandlers as Set<EffectHandler<Effect, Msg>>
)

/** @see StoreParams */
@Suppress("UNCHECKED_CAST")
inline fun <State : Any, Msg : Any, Effect : Any, reified ExternalMsg : Msg, reified InternalMsg : Msg> StoreParams(
    crossinline init: (previous: State?) -> Pair<State, Set<Effect>>,
    externalUpdate: Update<State, ExternalMsg, Effect>,
    internalUpdate: Update<State, InternalMsg, Effect>,
    effectHandler: EffectHandler<Effect, InternalMsg>,
) = StoreParams(
    init = init,
    update = { msg, state ->
        when (msg) {
            is ExternalMsg -> externalUpdate(msg, state)
            is InternalMsg -> internalUpdate(msg, state)
            else -> error("Unknown type $msg")
        }
    },
    effectHandler = effectHandler as EffectHandler<Effect, Msg>
)
