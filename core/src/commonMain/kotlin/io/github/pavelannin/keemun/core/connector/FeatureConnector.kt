package io.github.pavelannin.keemun.core.connector

import io.github.pavelannin.keemun.core.store.Store

/** Represents a holder of [Store]. */
interface FeatureConnector<State : Any, Msg : Any> : Store<State, Msg>
