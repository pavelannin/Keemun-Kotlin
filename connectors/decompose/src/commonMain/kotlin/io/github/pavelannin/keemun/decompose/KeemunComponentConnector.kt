package io.github.pavelannin.keemun.decompose

import com.arkivanov.decompose.ComponentContext
import io.github.pavelannin.keemun.core.connector.FeatureConnector
import io.github.pavelannin.keemun.core.connector.FeatureParams
import io.github.pavelannin.keemun.core.store.Store
import io.github.pavelannin.keemun.core.store.transform
import io.github.pavelannin.keemun.decompose.owners.CoroutineScopeOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.KSerializer

/**
 * Represents a holder of [Store] under [ComponentContext]. The implementation of this interface saves and restores the state of
 * the [Store] through [ComponentContext.stateKeeper].
 *
 * @see FeatureConnector
 */
interface KeemunComponentConnector<State : Any, Msg : Any> : ComponentContext, CoroutineScopeOwner, FeatureConnector<State, Msg> {

    /**
     * Represents the converter.
     *
     * @param perform converts from [In] to [Out].
     * @param perform converts from [Out] to [In].
     */
    data class Converter<In, Out>(
        val perform: (In) -> Out,
        val reverse: (Out) -> In,
    ) {
        companion object {
            operator fun <T> invoke(): Converter<T, T> = Converter(perform = { it }, reverse =  { it })
        }
    }

    /** Options that determine the initialization of the [KeemunComponentConnector]. */
    enum class StartedOptions {

        /** [Store] is started immediately. */
        Eagerly,

        /** [Store] is started on the first call. */
        Lazily
    }

    companion object {
        const val KEY_FEATURE_STATE = "FEATURE_STATE"
    }
}

/**
 * Creates a [KeemunComponentConnector] without saving a [State].
 *
 * @param componentContext The context for aggregating the creation of [KeemunComponentConnector].
 * @param storeCreator The function for creating the [Store].
 * @param startedOptions Options that determine the initialization of the [Store].
 *
 * @see KeemunComponentConnector
 * @see KeemunComponentConnector.StartedOptions
 */
fun <State : Any, Msg : Any> KeemunComponentConnector(
    componentContext: ComponentContext,
    storeCreator: CoroutineScope.(savedState: State?) -> Store<State, Msg>,
    startedOptions: KeemunComponentConnector.StartedOptions = KeemunComponentConnector.StartedOptions.Lazily,
): KeemunComponentConnector<State, Msg> = NoSavedStateComponentConnector(
    componentContext = componentContext,
    creatorStoreHolder = { StoreHolder(startedOptions) { storeCreator(this, null) } },
)

/**
 * Creates a [KeemunComponentConnector] with saving a [State].
 *
 * @param componentContext The context for aggregating the creation of [KeemunComponentConnector].
 * @param storeCreator The function for creating the [Store].
 * @param stateSerializer The serializer used for saving and restoring the state.
 * @param startedOptions Options that determine the initialization of the [Store].
 * @param savedStateKey Unique name with which the [State] will be saved.
 *
 * @see KeemunComponentConnector
 * @see KeemunComponentConnector.StartedOptions
 */
fun <State : Any, Msg : Any> KeemunComponentConnector(
    componentContext: ComponentContext,
    storeCreator: CoroutineScope.(savedState: State?) -> Store<State, Msg>,
    stateSerializer: KSerializer<State>,
    startedOptions: KeemunComponentConnector.StartedOptions = KeemunComponentConnector.StartedOptions.Lazily,
    savedStateKey: String = KeemunComponentConnector.KEY_FEATURE_STATE,
): KeemunComponentConnector<State, Msg> = SavedStateComponentConnector(
    componentContext = componentContext,
    creatorStoreHolder = { savedState -> StoreHolder(startedOptions) { storeCreator(this, savedState) } },
    savedStateSerializer = stateSerializer,
    savedStateConverter = KeemunComponentConnector.Converter(),
    savedStateKey = savedStateKey,
)

/**
 * Creates a [KeemunComponentConnector] with saving a [State].
 *
 * @param componentContext The context for aggregating the creation of [KeemunComponentConnector].
 * @param storeCreator The function for creating the [Store].
 * @param savedStateSerializer The serializer used for saving and restoring the state.
 * @param savedStateConverter Converter used before saving or restoring a [State].
 * @param startedOptions Options that determine the initialization of the [Store].
 * @param savedStateKey Unique name with which the [State] will be saved.
 *
 * @see KeemunComponentConnector
 * @see KeemunComponentConnector.StartedOptions
 */
fun <SavedState: Any, State : Any, Msg : Any> KeemunComponentConnector(
    componentContext: ComponentContext,
    storeCreator: CoroutineScope.(savedState: State?) -> Store<State, Msg>,
    savedStateSerializer: KSerializer<SavedState>,
    savedStateConverter: KeemunComponentConnector.Converter<State?, SavedState?>,
    startedOptions: KeemunComponentConnector.StartedOptions = KeemunComponentConnector.StartedOptions.Lazily,
    savedStateKey: String = KeemunComponentConnector.KEY_FEATURE_STATE,
): KeemunComponentConnector<State, Msg> = SavedStateComponentConnector(
    componentContext = componentContext,
    creatorStoreHolder = { savedState -> StoreHolder(startedOptions) { storeCreator(this, savedState) } },
    savedStateSerializer = savedStateSerializer,
    savedStateConverter = savedStateConverter,
    savedStateKey = savedStateKey,
)

/**
 * Creates a [KeemunComponentConnector] without saving a [State]..
 *
 * @param componentContext The context for aggregating the creation of [KeemunComponentConnector].
 * @param featureParams Parameters used for creation.
 * @param startedOptions Options that determine the initialization of the [Store].
 *
 * @see KeemunComponentConnector
 */
inline fun <State : Any, Msg : Any, ViewState : Any, ExternalMsg : Msg> KeemunComponentConnector(
    componentContext: ComponentContext,
    featureParams: FeatureParams<State, Msg, ViewState, ExternalMsg>,
    startedOptions: KeemunComponentConnector.StartedOptions = KeemunComponentConnector.StartedOptions.Lazily,
): KeemunComponentConnector<ViewState, ExternalMsg> = object : KeemunComponentConnector<ViewState, ExternalMsg>,
    ComponentContext by componentContext,
    CoroutineScopeOwner by componentContext.CoroutineScopeOwner(),
    Store<ViewState, ExternalMsg> by KeemunComponentConnector(
        componentContext = componentContext,
        storeCreator = { savedState: State? -> Store(savedState, params = featureParams.storeParams, coroutineScope = this) },
        startedOptions = startedOptions,
    ).transform(
        stateTransform = featureParams.viewStateTransform,
        messageTransform = featureParams.externalMessageTransform,
    ){}

/**
 * Creates a [KeemunComponentConnector] with saving a [State]..
 *
 * @param componentContext The context for aggregating the creation of [KeemunComponentConnector].
 * @param featureParams Parameters used for creation.
 * @param stateSerializer The serializer used for saving and restoring the state.
 * @param startedOptions Options that determine the initialization of the [Store].
 * @param savedStateKey Unique name with which the [State] will be saved.
 *
 * @see KeemunComponentConnector
 * @see KeemunComponentConnector.StartedOptions
 */
inline fun <State : Any, Msg : Any, ViewState : Any, ExternalMsg : Msg> KeemunComponentConnector(
    componentContext: ComponentContext,
    featureParams: FeatureParams<State, Msg, ViewState, ExternalMsg>,
    stateSerializer: KSerializer<State>,
    startedOptions: KeemunComponentConnector.StartedOptions = KeemunComponentConnector.StartedOptions.Lazily,
    savedStateKey: String = KeemunComponentConnector.KEY_FEATURE_STATE,
): KeemunComponentConnector<ViewState, ExternalMsg> = object : KeemunComponentConnector<ViewState, ExternalMsg>,
    ComponentContext by componentContext,
    CoroutineScopeOwner by componentContext.CoroutineScopeOwner(),
    Store<ViewState, ExternalMsg> by KeemunComponentConnector(
        componentContext = componentContext,
        storeCreator = { savedState: State? -> Store(savedState, params = featureParams.storeParams, coroutineScope = this) },
        stateSerializer = stateSerializer,
        startedOptions = startedOptions,
        savedStateKey = savedStateKey,
    ).transform(
        stateTransform = featureParams.viewStateTransform,
        messageTransform = featureParams.externalMessageTransform,
    ){}

/**
 * Creates a [KeemunComponentConnector] with saving a [State]..
 *
 * @param componentContext The context for aggregating the creation of [KeemunComponentConnector].
 * @param featureParams Parameters used for creation.
 * @param savedStateSerializer The serializer used for saving and restoring the state.
 * @param savedStateConverter Converter used before saving or restoring a [State].
 * @param startedOptions Options that determine the initialization of the [Store].
 * @param savedStateKey Unique name with which the [State] will be saved.
 *
 * @see KeemunComponentConnector
 * @see KeemunComponentConnector.StartedOptions
 */
inline fun <SavedState: Any, State : Any, Msg : Any, ViewState : Any, ExternalMsg : Msg> KeemunComponentConnector(
    componentContext: ComponentContext,
    featureParams: FeatureParams<State, Msg, ViewState, ExternalMsg>,
    savedStateSerializer: KSerializer<SavedState>,
    savedStateConverter: KeemunComponentConnector.Converter<State?, SavedState?>,
    startedOptions: KeemunComponentConnector.StartedOptions = KeemunComponentConnector.StartedOptions.Lazily,
    savedStateKey: String = KeemunComponentConnector.KEY_FEATURE_STATE,
): KeemunComponentConnector<ViewState, ExternalMsg> = object : KeemunComponentConnector<ViewState, ExternalMsg>,
    ComponentContext by componentContext,
    CoroutineScopeOwner by componentContext.CoroutineScopeOwner(),
    Store<ViewState, ExternalMsg> by KeemunComponentConnector(
        componentContext = componentContext,
        storeCreator = { savedState: State? -> Store(savedState, params = featureParams.storeParams, coroutineScope = this) },
        savedStateSerializer = savedStateSerializer,
        savedStateConverter = savedStateConverter,
        startedOptions = startedOptions,
        savedStateKey = savedStateKey,
    ).transform(
        stateTransform = featureParams.viewStateTransform,
        messageTransform = featureParams.externalMessageTransform,
    ){}

private class NoSavedStateComponentConnector<State : Any, Msg : Any>(
    componentContext: ComponentContext,
    creatorStoreHolder: CoroutineScope.() -> StoreHolder<State, Msg>,
) : KeemunComponentConnector<State, Msg>,
    ComponentContext by componentContext,
    CoroutineScopeOwner by componentContext.CoroutineScopeOwner(),
    Store<State, Msg> {

    private val storeHolder = creatorStoreHolder(coroutineScope)

    override val state: StateFlow<State> get() = storeHolder.store.state
    override val scope: CoroutineScope get() = storeHolder.store.scope

    override fun dispatch(msg: Msg) { storeHolder.store dispatch msg }
    override suspend fun syncDispatch(msg: Msg) { storeHolder.store syncDispatch msg }
}

private class SavedStateComponentConnector<SavedState : Any, State : Any, Msg : Any>(
    componentContext: ComponentContext,
    creatorStoreHolder: CoroutineScope.(savedState: State?) -> StoreHolder<State, Msg>,
    savedStateSerializer: KSerializer<SavedState>,
    savedStateConverter: KeemunComponentConnector.Converter<State?, SavedState?>,
    savedStateKey: String,
) : KeemunComponentConnector<State, Msg>,
    ComponentContext by componentContext,
    CoroutineScopeOwner by componentContext.CoroutineScopeOwner(),
    Store<State, Msg> {

    private val storeHolder = creatorStoreHolder(
        coroutineScope,
        savedStateConverter.reverse(stateKeeper.consume(savedStateKey, savedStateSerializer)),
    )

    override val state: StateFlow<State> get() = storeHolder.store.state
    override val scope: CoroutineScope get() = storeHolder.store.scope

    init {
        if (!stateKeeper.isRegistered(savedStateKey)) {
            stateKeeper.register(savedStateKey, savedStateSerializer) { savedStateConverter.perform(state.value) }
        }
    }

    override fun dispatch(msg: Msg) { storeHolder.store dispatch msg }
    override suspend fun syncDispatch(msg: Msg) { storeHolder.store syncDispatch msg }
}

private sealed class StoreHolder<State : Any, in Msg : Any> {
    abstract val store: Store<State, Msg>

    private class Eagerl<State : Any, in Msg : Any>(
        override val store: Store<State, Msg>,
    ) : StoreHolder<State, Msg>()

    private class Lazily<State : Any, in Msg : Any>(
        creator: () -> Store<State, Msg>,
    ) : StoreHolder<State, Msg>() {
        override val store: Store<State, Msg> by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED, initializer = creator)
    }

    companion object {
        operator fun <State : Any, Msg : Any> invoke(
            startedOptions: KeemunComponentConnector.StartedOptions,
            creator: () -> Store<State, Msg>,
        ): StoreHolder<State, Msg> = when (startedOptions) {
            KeemunComponentConnector.StartedOptions.Eagerly -> Eagerl(creator())
            KeemunComponentConnector.StartedOptions.Lazily -> Lazily(creator)
        }
    }
}
