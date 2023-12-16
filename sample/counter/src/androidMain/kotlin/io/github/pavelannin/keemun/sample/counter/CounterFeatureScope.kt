package io.github.pavelannin.keemun.sample.counter

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import io.github.pavelannin.keemun.core.store.Store
import io.github.pavelannin.keemun.decompose.KeemunComponentFeatureConnector
import io.github.pavelannin.keemun.sample.counter.features.counter.Counter
import io.github.pavelannin.keemun.sample.counter.features.counter.CounterViewState
import io.github.pavelannin.keemun.sample.counter.features.counter.ExternalMsg
import io.github.pavelannin.keemun.sample.counter.features.counter.counterFeatureParams

actual class CounterFeatureScope {

    fun counter(componentContext: ComponentContext) = KeemunComponentFeatureConnector(
        componentContext = componentContext,
        featureParams = counterFeatureParams()
    )

    @Composable
    fun CounterRender(store: Store<CounterViewState, ExternalMsg>) { Counter(store) }
}
