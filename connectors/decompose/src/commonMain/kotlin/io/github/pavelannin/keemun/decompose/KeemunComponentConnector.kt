package io.github.pavelannin.keemun.decompose

import com.arkivanov.decompose.ComponentContext
import io.github.pavelannin.keemun.core.connector.FeatureConnector
import io.github.pavelannin.keemun.core.connector.FeatureParams
import io.github.pavelannin.keemun.core.connector.FeatureStartedOptions
import io.github.pavelannin.keemun.core.store.Store
import io.github.pavelannin.keemun.core.store.transform
import io.github.pavelannin.keemun.decompose.owners.CoroutineScopeOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

/**
 * Represents a holder of [Store] under [ComponentContext]. The implementation of this interface saves and restores the state of
 * the [Store] through [ComponentContext.stateKeeper].
 *
 * @see FeatureConnector
 */
interface KeemunComponentConnector<State : Any, Msg : Any> : ComponentContext, CoroutineScopeOwner, FeatureConnector<State, Msg>

/**
 * Creates a [KeemunComponentConnector].
 *
 * @param componentContext The context for aggregating the creation of [KeemunComponentConnector].
 * @param storeCreator The function for creating the [Store].
 * @param stateSerializer The serializer used for saving and restoring the state.
 *
 * @see KeemunComponentConnector
 */
fun <State : Any, Msg : Any> KeemunComponentConnector(
    componentContext: ComponentContext,
    storeCreator: (savedState: State?, scope: CoroutineScope) -> Lazy<Store<State, Msg>>,
    stateSerializer: KSerializer<State>,
): KeemunComponentConnector<State, Msg> = DefaultComponentConnector(
    componentContext = componentContext,
    storeCreator = storeCreator,
    stateSerializer = stateSerializer,
)

/**
 * Creates a [KeemunComponentConnector].
 *
 * @param componentContext The context for aggregating the creation of [KeemunComponentConnector].
 * @param storeCreator The function for creating the [Store].
 *
 * @see KeemunComponentConnector
 */
@OptIn(InternalSerializationApi::class)
inline fun <reified State : Any, Msg : Any> KeemunComponentConnector(
    componentContext: ComponentContext,
    noinline storeCreator: (savedState: State?, scope: CoroutineScope) -> Lazy<Store<State, Msg>>,
) = KeemunComponentConnector(
    componentContext = componentContext,
    storeCreator = storeCreator,
    stateSerializer = State::class.serializer(),
)

/**
 * Creates a [KeemunComponentConnector].
 *
 * @param componentContext The context for aggregating the creation of [KeemunComponentConnector].
 * @param featureParams Parameters used for creation.
 * @param stateSerializer The serializer used for saving and restoring the state.
 *
 * @see KeemunComponentConnector
 */
@OptIn(InternalSerializationApi::class)
inline fun <reified State : Any, Msg : Any, ViewState : Any, ExternalMsg : Msg> KeemunComponentConnector(
    componentContext: ComponentContext,
    featureParams: FeatureParams<State, Msg, ViewState, ExternalMsg>,
    stateSerializer: KSerializer<State> = State::class.serializer(),
): KeemunComponentConnector<ViewState, ExternalMsg> = object : KeemunComponentConnector<ViewState, ExternalMsg>,
    ComponentContext by componentContext,
    CoroutineScopeOwner by componentContext.CoroutineScopeOwner(),
    Store<ViewState, ExternalMsg> by KeemunComponentConnector(
        componentContext = componentContext,
        storeCreator = { savedState, scope ->
            lazy { Store(savedState = savedState, params = featureParams.storeParams, coroutineScope = scope) }.let { lazyStore ->
                when (featureParams.startedOptions) {
                    FeatureStartedOptions.Eagerly -> lazyStore.apply { value }
                    FeatureStartedOptions.Lazily -> lazyStore
                }
            }
        },
        stateSerializer = stateSerializer,
    ).transform(
        stateTransform = featureParams.viewStateTransform,
        messageTransform = featureParams.externalMessageTransform,
    ){}

private class DefaultComponentConnector<State : Any, Msg : Any>(
    componentContext: ComponentContext,
    storeCreator: (savedState: State?, scope: CoroutineScope) -> Lazy<Store<State, Msg>>,
    stateSerializer: KSerializer<State>,
) : KeemunComponentConnector<State, Msg>,
    ComponentContext by componentContext,
    CoroutineScopeOwner by componentContext.CoroutineScopeOwner(),
    Store<State, Msg> {

    private val store by storeCreator(stateKeeper.consume(KEY_FEATURE_STATE, stateSerializer), this)

    override val state: StateFlow<State> get() = store.state
    override val scope: CoroutineScope get() = store.scope

    init {
        if (!stateKeeper.isRegistered(KEY_FEATURE_STATE)) {
            stateKeeper.register(KEY_FEATURE_STATE, stateSerializer) { state.value }
        }
    }

    override fun dispatch(msg: Msg) { store dispatch msg }
    override suspend fun syncDispatch(msg: Msg) { store syncDispatch msg }

    private companion object {
        const val KEY_FEATURE_STATE = "FEATURE_STATE"
    }
}
