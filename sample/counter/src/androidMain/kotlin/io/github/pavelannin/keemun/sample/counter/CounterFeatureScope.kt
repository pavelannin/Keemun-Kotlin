package io.github.pavelannin.keemun.sample.counter

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import io.github.pavelannin.keemun.core.store.Store
import io.github.pavelannin.keemun.decompose.KeemunComponentConnector
import io.github.pavelannin.keemun.sample.counter.features.counter.Counter
import io.github.pavelannin.keemun.sample.counter.features.counter.CounterState
import io.github.pavelannin.keemun.sample.counter.features.counter.CounterViewState
import io.github.pavelannin.keemun.sample.counter.features.counter.ExternalMsg
import io.github.pavelannin.keemun.sample.counter.features.counter.counterFeatureParams

actual class CounterFeatureScope {

    fun counter(componentContext: ComponentContext) = KeemunComponentConnector(
        componentContext = componentContext,
        featureParams = counterFeatureParams(),
        stateSerializer = CounterState.serializer(),
    )

    @Composable
    fun CounterRender(store: Store<CounterViewState, ExternalMsg>) { Counter(store) }
}
