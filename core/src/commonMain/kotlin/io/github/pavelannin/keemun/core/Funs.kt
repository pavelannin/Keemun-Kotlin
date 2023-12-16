package io.github.pavelannin.keemun.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.native.HiddenFromObjC

/** Process [effects] in [EffectHandler]. */
@HiddenFromObjC
fun <Effect : Any, Msg : Any> EffectHandler<Effect, Msg>.process(
    effects: Set<Effect>,
    coroutineScope: CoroutineScope,
    dispatch: Dispatch<Msg>
) {
    for (effect in effects) coroutineScope.launch { invoke(effect, dispatch) }
}
