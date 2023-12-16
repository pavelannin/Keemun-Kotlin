package io.github.pavelannin.keemun.core.connector

import io.github.pavelannin.keemun.core.StateTransform
import io.github.pavelannin.keemun.core.store.Store
import io.github.pavelannin.keemun.core.store.StoreParams
import kotlin.native.HiddenFromObjC
import kotlin.native.ObjCName

/**
 * Required parameters for creating [FeatureConnector].
 *
 * @param storeParams Parameters for creating [Store].
 * @param viewStateTransform Function that converts [State] into [ViewState]. Used for generating [ViewState].
 * Called whenever [State] changes.
 * @param externalMessageTransform Function that converts [ExternalMsg] into [Msg]. Used for passing messages to the [Store].
 * Called when messages are passed to the [Store].
 * @param startedOptions Options that determine the initialization of the [Store].
 *
 * @see StoreParams
 * @see StateTransform
 * @see FeatureStartedOptions
 */
@ObjCName(swiftName = "KeemunFeatureParams")
data class FeatureParams<State : Any, Msg : Any, ViewState : Any, ExternalMsg : Any>(
    val storeParams: StoreParams<State, Msg, *, *>,
    val viewStateTransform: StateTransform<State, ViewState>,
    val externalMessageTransform: (ExternalMsg) -> Msg,
    val startedOptions: FeatureStartedOptions,
)

/** @see FeatureParams */
@HiddenFromObjC
inline fun <State : Any, Msg : Any, ViewState : Any, ExternalMsg : Msg> FeatureParams(
    storeParams: StoreParams<State, Msg, *, *>,
    viewStateTransform: StateTransform<State, ViewState>,
    startedOptions: FeatureStartedOptions = FeatureStartedOptions.Lazily,
) = FeatureParams<State, Msg, ViewState, ExternalMsg>(
    storeParams = storeParams,
    viewStateTransform = viewStateTransform,
    externalMessageTransform = { it },
    startedOptions = startedOptions,
)

/** @see FeatureParams */
@HiddenFromObjC
inline fun <State : Any, Msg : Any, ExternalMsg : Any> FeatureParams(
    storeParams: StoreParams<State, Msg, *, *>,
    noinline externalMessageTransform: (ExternalMsg) -> Msg,
    startedOptions: FeatureStartedOptions = FeatureStartedOptions.Lazily,
) = FeatureParams<State, Msg, State, ExternalMsg>(
    storeParams = storeParams,
    viewStateTransform = { it },
    externalMessageTransform = externalMessageTransform,
    startedOptions = startedOptions,
)

/** @see FeatureParams */
@HiddenFromObjC
inline fun <State : Any, Msg : Any, ExternalMsg : Msg> FeatureParams(
    storeParams: StoreParams<State, Msg, *, *>,
    startedOptions: FeatureStartedOptions = FeatureStartedOptions.Lazily,
) = FeatureParams<State, Msg, State, ExternalMsg>(
    storeParams = storeParams,
    viewStateTransform = { it },
    externalMessageTransform = { it },
    startedOptions = startedOptions,
)
