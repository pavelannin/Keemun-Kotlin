package io.github.pavelannin.keemun.sample.counter.features.counter

import io.github.pavelannin.keemun.core.connector.FeatureParams
import io.github.pavelannin.keemun.core.store.StoreParams
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

internal fun counterFeatureParams(): FeatureParams<CounterState, CounterMsg, CounterViewState, ExternalMsg> = FeatureParams(
    storeParams = StoreParams<CounterState, CounterMsg, CounterEffect, ExternalMsg, InternalMsg>(
        start = { savedState -> (savedState ?: CounterState(syncCount = 0, asyncCount = 0, isAsyncRunning = false)) to emptySet() },
        externalUpdate = { msg, state ->
            when (msg) {
                ExternalMsg.IncrementSync ->
                    state.copy(syncCount = state.syncCount.inc()) to emptySet()

                ExternalMsg.DecrementSync ->
                    state.copy(syncCount = state.syncCount.dec()) to emptySet()

                ExternalMsg.IncrementAsync ->
                    if (state.isAsyncRunning) {
                        state to emptySet()
                    } else {
                        state.copy(isAsyncRunning = true) to setOf(CounterEffect.Increment(state.asyncCount))
                    }

                ExternalMsg.DecrementAsync ->
                    if (state.isAsyncRunning) {
                        state to emptySet()
                    } else {
                        state.copy(isAsyncRunning = true) to setOf(CounterEffect.Decrement(state.asyncCount))
                    }
            }
        },
        internalUpdate = { msg, state ->
            when (msg) {
                is InternalMsg.CompletedAsyncOperation ->
                    state.copy(asyncCount = msg.value, isAsyncRunning = false) to emptySet()
            }
        },
        effectHandler = { effect, dispatch ->
            when (effect) {
                is CounterEffect.Decrement -> {
                    delay(1_000)
                    dispatch(InternalMsg.CompletedAsyncOperation(effect.value.dec()))
                }

                is CounterEffect.Increment -> {
                    delay(1_000)
                    dispatch(InternalMsg.CompletedAsyncOperation(effect.value.inc()))
                }
            }
        },
    ),
    viewStateTransform = { state ->
        CounterViewState(
            syncCount = state.syncCount.toString(),
            asyncCount = state.asyncCount.toString(),
            isAsyncRunning = state.isAsyncRunning,
        )
    }
)

@Serializable
data class CounterState(
    val syncCount: Int,
    val asyncCount: Int,
    val isAsyncRunning: Boolean,
)

data class CounterViewState(
    val syncCount: String,
    val asyncCount: String,
    val isAsyncRunning: Boolean,
)

sealed interface CounterMsg

sealed class ExternalMsg : CounterMsg {
    data object IncrementSync : ExternalMsg()
    data object DecrementSync : ExternalMsg()
    data object IncrementAsync : ExternalMsg()
    data object DecrementAsync : ExternalMsg()
}

private sealed class InternalMsg : CounterMsg {
    data class CompletedAsyncOperation(val value: Int) : InternalMsg()
}

private sealed class CounterEffect {
    data class Increment(val value: Int) : CounterEffect()
    data class Decrement(val value: Int) : CounterEffect()
}
