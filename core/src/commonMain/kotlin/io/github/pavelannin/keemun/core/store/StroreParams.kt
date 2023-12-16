package io.github.pavelannin.keemun.core.store

import io.github.pavelannin.keemun.core.EffectHandler
import io.github.pavelannin.keemun.core.Init
import io.github.pavelannin.keemun.core.PreInitEffect
import io.github.pavelannin.keemun.core.Update
import kotlin.native.HiddenFromObjC

/**
 * Required parameters for creating [Store].
 *
 * @see PreInitEffect
 * @see Init
 * @see Update
 * @see EffectHandler
 */
@HiddenFromObjC
data class StoreParams<State : Any, Msg : Any, Effect : Any, Deps : Any>(
    val preEffectInit: PreInitEffect<Deps>,
    val init: Init<State, Effect, Deps>,
    val update: Update<State, Msg, Effect>,
    val effectHandlers: Set<EffectHandler<Effect, Msg>>,
)

/** @see StoreParams */
@HiddenFromObjC
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
@HiddenFromObjC
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
@HiddenFromObjC
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

/** @see StoreParams */
@HiddenFromObjC
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
@HiddenFromObjC
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
@HiddenFromObjC
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
@HiddenFromObjC
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
