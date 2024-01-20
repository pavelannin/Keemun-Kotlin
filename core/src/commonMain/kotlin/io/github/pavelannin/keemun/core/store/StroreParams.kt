package io.github.pavelannin.keemun.core.store

import io.github.pavelannin.keemun.core.EffectHandler
import io.github.pavelannin.keemun.core.Start
import io.github.pavelannin.keemun.core.Update
import kotlin.native.HiddenFromObjC

/**
 * Required parameters for creating [Store].
 *
 * @see Start
 * @see Update
 * @see EffectHandler
 */
@HiddenFromObjC
data class StoreParams<State : Any, Msg : Any, Effect : Any>(
    val start: Start<State, Effect>,
    val update: Update<State, Msg, Effect>,
    val effectHandlers: Set<EffectHandler<Effect, Msg>>,
)

/** @see StoreParams */
@HiddenFromObjC
inline fun <State : Any, Msg : Any, Effect : Any> StoreParams(
    start: Start<State, Effect>,
    update: Update<State, Msg, Effect>,
    effectHandler: EffectHandler<Effect, Msg>,
): StoreParams<State, Msg, Effect> = StoreParams(
    start = start,
    update = update,
    effectHandlers = setOf(effectHandler)
)

/** @see StoreParams */
@HiddenFromObjC
inline fun <State : Any, Msg : Any, Effect : Any, reified ExternalMsg : Msg, reified InternalMsg : Msg> StoreParams(
    start: Start<State, Effect>,
    externalUpdate: Update<State, ExternalMsg, Effect>,
    internalUpdate: Update<State, InternalMsg, Effect>,
    effectHandlers: Set<EffectHandler<Effect, InternalMsg>>,
): StoreParams<State, Msg, Effect> = StoreParams(
    start = start,
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
inline fun <State : Any, Msg : Any, Effect : Any, reified ExternalMsg : Msg, reified InternalMsg : Msg> StoreParams(
    start: Start<State, Effect>,
    externalUpdate: Update<State, ExternalMsg, Effect>,
    internalUpdate: Update<State, InternalMsg, Effect>,
    effectHandler: EffectHandler<Effect, InternalMsg>,
): StoreParams<State, Msg, Effect> = StoreParams(
    start = start,
    update = { msg, state ->
        when (msg) {
            is ExternalMsg -> externalUpdate(msg, state)
            is InternalMsg -> internalUpdate(msg, state)
            else -> error("Unknown type $msg")
        }
    },
    effectHandler = effectHandler
)
