package io.github.pavelannin.keemun.sample.counter

import io.github.pavelannin.keemun.sample.counter.features.counter.counterFeatureParams
import io.github.pavelannin.keemun.swiftui.KeemunNativeFeatureConnector

actual class CounterFeatureScope {
    fun counter() = KeemunNativeFeatureConnector(featureParams = counterFeatureParams())
}
