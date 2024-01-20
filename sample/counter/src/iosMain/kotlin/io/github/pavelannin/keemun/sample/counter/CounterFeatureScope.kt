package io.github.pavelannin.keemun.sample.counter

import io.github.pavelannin.keemun.sample.counter.features.counter.counterFeatureParams
import io.github.pavelannin.keemun.swiftui.KeemunNativeConnector

actual class CounterFeatureScope {
    fun counter() = KeemunNativeConnector(featureParams = counterFeatureParams())
}
