package io.github.pavelannin.keemun.core.completeness

import io.github.pavelannin.keemun.core.EffectHandler
import io.github.pavelannin.keemun.core.Update
import io.github.pavelannin.keemun.core.store.Store
import io.github.pavelannin.keemun.core.store.StoreParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.asFlow

data class CompletenessState(val flow1: Int, val flow2: Int, val flow3: Int)

sealed class CompletenessMsg {
    data object IncFlow1 : CompletenessMsg()
    data object IncFlow2 : CompletenessMsg()
    data object IncFlow3 : CompletenessMsg()
}

sealed class CompletenessEffect {
    data class StartIncFlow1(val repeatCount: Int) : CompletenessEffect()
    data class StartIncFlow2(val repeatCount: Int) : CompletenessEffect()
    data class StartIncFlow3(val repeatCount: Int) : CompletenessEffect()
}

private val update = Update<CompletenessState, CompletenessMsg, CompletenessEffect> { msg, model ->
    when (msg) {
        CompletenessMsg.IncFlow1 -> model.copy(flow1 = model.flow1 + 1) to emptySet()
        CompletenessMsg.IncFlow2 -> model.copy(flow2 = model.flow2 + 1) to emptySet()
        CompletenessMsg.IncFlow3 -> model.copy(flow3 = model.flow3 + 1) to emptySet()
    }
}

private val effectHandler = EffectHandler<CompletenessEffect, CompletenessMsg> { effect, dispatch ->
    when (effect) {
        is CompletenessEffect.StartIncFlow1 -> (1..effect.repeatCount).asFlow().collect { dispatch(CompletenessMsg.IncFlow1) }
        is CompletenessEffect.StartIncFlow2 -> (1..effect.repeatCount).asFlow().collect { dispatch(CompletenessMsg.IncFlow2) }
        is CompletenessEffect.StartIncFlow3 -> (1..effect.repeatCount).asFlow().collect { dispatch(CompletenessMsg.IncFlow3) }
    }
}

fun store(scope: CoroutineScope, repeatCount: Int) = Store(
    previousState = null,
    coroutineScope = scope,
    params = StoreParams(
        init = { previous ->
            val state = previous ?: CompletenessState(flow1 = 0, flow2 = 0, flow3 = 0)
            state to setOf(
                CompletenessEffect.StartIncFlow1(repeatCount),
                CompletenessEffect.StartIncFlow2(repeatCount),
                CompletenessEffect.StartIncFlow3(repeatCount)
            )
        },
        update = update,
        effectHandler = effectHandler
    )
)
