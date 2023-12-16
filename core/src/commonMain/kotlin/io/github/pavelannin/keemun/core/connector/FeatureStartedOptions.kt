package io.github.pavelannin.keemun.core.connector

import io.github.pavelannin.keemun.core.store.Store
import kotlin.native.HiddenFromObjC

/** Options that determine the initialization of the [FeatureConnector]. */
@HiddenFromObjC
sealed class FeatureStartedOptions {

    /** [Store] is started immediately. */
    data object Eagerly : FeatureStartedOptions()

    /** [Store] is started on the first call. */
    data object Lazily : FeatureStartedOptions()
}
