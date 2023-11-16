package io.github.pavelannin.keemun

import com.arkivanov.decompose.ComponentContext
import io.github.pavelannin.keemun.core.StateTransform
import io.github.pavelannin.keemun.core.store.Store
import io.github.pavelannin.keemun.core.store.StoreParams
import io.github.pavelannin.keemun.owners.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

interface Feature<ViewState : Any, Msg : Any> {
    val viewState: StateFlow<ViewState>

    infix fun dispatch(msg: Msg)
    suspend infix fun syncDispatch(msg: Msg)
}

@OptIn(InternalSerializationApi::class)
inline fun <reified State : Any, Msg : Any, Effect : Any, Deps : Any, ViewState : Any, ExternalMsg : Msg> Feature(
    componentContext: ComponentContext,
    params: StoreParams<State, Msg, Effect, Deps>,
    stateTransform: StateTransform<State, ViewState>,
    stateSerializer: KSerializer<State> = State::class.serializer(),
    coroutineScope: CoroutineScope = componentContext.coroutineScope(),
): Feature<ViewState, ExternalMsg> = FeatureComponentContext(
    componentContext = componentContext,
    params = params,
    stateSerializer = stateSerializer,
    stateTransform = stateTransform,
    coroutineScope = coroutineScope,
)

@OptIn(InternalSerializationApi::class)
inline fun <reified State : Any, Msg : Any, Effect : Any, Deps : Any, ExternalMsg : Msg> Feature(
    componentContext: ComponentContext,
    params: StoreParams<State, Msg, Effect, Deps>,
    stateSerializer: KSerializer<State> = State::class.serializer(),
    coroutineScope: CoroutineScope = componentContext.coroutineScope(),
): Feature<State, ExternalMsg> = Feature(
    componentContext = componentContext,
    params = params,
    stateSerializer = stateSerializer,
    stateTransform = { it },
    coroutineScope = coroutineScope,
)

class FeatureComponentContext<State : Any, Msg : Any, Effect : Any, Deps : Any, ViewState : Any, ExternalMsg : Msg>(
    componentContext: ComponentContext,
    params: StoreParams<State, Msg, Effect, Deps>,
    stateSerializer: KSerializer<State>,
    private val stateTransform: StateTransform<State, ViewState>,
    private val coroutineScope: CoroutineScope,
) : ComponentContext by componentContext, Feature<ViewState, ExternalMsg> {
    private val store = Store(
        previousState = stateKeeper.consume(KEY_FEATURE_STATE, stateSerializer),
        params = params,
        coroutineScope = coroutineScope,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    override val viewState: StateFlow<ViewState>
        get() = store.state
            .mapLatest(stateTransform::invoke)
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.Lazily,
                initialValue = runBlocking { stateTransform(store.state.value) },
            )

    init {
        stateKeeper.register(KEY_FEATURE_STATE, stateSerializer) { store.state.value }
    }

    override fun dispatch(msg: ExternalMsg) { store dispatch msg }

    override suspend fun syncDispatch(msg: ExternalMsg) { store syncDispatch msg }

    companion object {
        private const val KEY_FEATURE_STATE = "FEATURE_STATE"
    }
}
