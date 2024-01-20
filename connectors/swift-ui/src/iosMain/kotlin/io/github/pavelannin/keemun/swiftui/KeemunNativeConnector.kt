package io.github.pavelannin.keemun.swiftui

import io.github.pavelannin.keemun.core.connector.FeatureConnector
import io.github.pavelannin.keemun.core.connector.FeatureParams
import io.github.pavelannin.keemun.core.connector.FeatureStartedOptions
import io.github.pavelannin.keemun.core.store.Store
import io.github.pavelannin.keemun.core.store.transform
import io.github.pavelannin.keemun.swiftui.interop.KotlinNativeStateFlow
import io.github.pavelannin.keemun.swiftui.interop.KotlinNativeSuspend
import io.github.pavelannin.keemun.swiftui.interop.asKotlinNativeStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Represents a holder of [Store]. The implementation of this interface does not save and restore the state of the [Store].
 *
 * @param storeCreator The function for creating the [Store].
 * @property nativeState The state of the [Store] is wrapped in [KotlinNativeStateFlow] to preserve the generic type.
 *
 * @see FeatureConnector
 */
class KeemunNativeConnector<State : Any, Msg : Any> (
    storeCreator: (scope: CoroutineScope) -> Lazy<Store<State, Msg>>,
): FeatureConnector<State, Msg>, CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private val store by storeCreator(this)

    override val state: StateFlow<State> get() = store.state
    override val scope: CoroutineScope get() = store.scope
    val nativeState: KotlinNativeStateFlow<State> get() = store.state.asKotlinNativeStateFlow()

    override fun dispatch(msg: Msg) { store dispatch msg }
    override suspend fun syncDispatch(msg: Msg) { store syncDispatch msg }

    /** Sending messages synchronously is wrapped in [KotlinNativeSuspend]. */
    fun nativeSyncDispatch(msg: Msg) = KotlinNativeSuspend { store.syncDispatch(msg) }

    /** Subscribes to changes in the state of the [Store]. Use this function to receive state updates in the user interface. */
    fun render(collector: (State) -> Unit) {
        launch { state.collect { withContext(Dispatchers.Main) { collector(it) } } }
    }
}

/**
 * Creates a [KeemunNativeConnector].
 *
 * @param featureParams Parameters used for creation.
 *
 * @see KeemunNativeConnector
 */
inline fun <reified State : Any, Msg : Any, ViewState : Any, ExternalMsg : Msg> KeemunNativeConnector(
    featureParams: FeatureParams<State, Msg, ViewState, ExternalMsg>,
): KeemunNativeConnector<ViewState, ExternalMsg> = KeemunNativeConnector(
    storeCreator = { scope ->
        lazy {
            Store(savedState = null, params = featureParams.storeParams, coroutineScope = scope)
                .transform(stateTransform = featureParams.viewStateTransform, messageTransform = featureParams.externalMessageTransform)
        }.let { lazyStore ->
            when (featureParams.startedOptions) {
                FeatureStartedOptions.Eagerly -> lazyStore.apply { value }
                FeatureStartedOptions.Lazily -> lazyStore
            }
        }
    }
)
