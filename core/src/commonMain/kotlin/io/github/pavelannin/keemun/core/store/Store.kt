package io.github.pavelannin.keemun.core.store

import io.github.pavelannin.keemun.core.process
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking

/**
 * A common interface, the implementation of which controls all entities and starts the entire processing mechanism.
 * [Msg] - messages with which we will transform [State].
 *
 * @property state Flow of state.
 * @property scope The main scope on which all coroutines will be launched.
 */
interface Store<State : Any, Msg : Any> {
    val state: StateFlow<State>
    val scope: CoroutineScope

    /** Sending messages asynchronously. */
    infix fun dispatch(msg: Msg)

    /** Sending messages synchronously. */
    suspend infix fun syncDispatch(msg: Msg)

    companion object {
        operator fun <State : Any, Msg : Any, Effect : Any, Deps : Any> invoke(
            previousState: State?,
            params: StoreParams<State, Msg, Effect, Deps>,
            coroutineScope: CoroutineScope,
        ): Store<State, Msg> = DefaultStore(
            previousState = previousState,
            params = params,
            coroutineScope = coroutineScope,
        )
    }
}

/**
 * [State] - common state, [Msg] - messages with which we will transform the [State].
 *
 * @param previousState Previous state. Not null if the process was killed by the system and restored to its previous state.
 */
private class DefaultStore<State : Any, Msg : Any, Effect : Any, Deps : Any>(
    previousState: State?,
    private val params: StoreParams<State, Msg, Effect, Deps>,
    coroutineScope: CoroutineScope,
) : Store<State, Msg> {

    private val _state: MutableStateFlow<State>
    private val _messages = MutableSharedFlow<Msg>(extraBufferCapacity = Int.MAX_VALUE)

    override val state: StateFlow<State> get() = _state.asStateFlow()
    override val scope: CoroutineScope = coroutineScope + Dispatchers.Default

    init {
        val (defaultState, startEffects) = runBlocking { params.init(previousState, params.preEffectInit()) }
        _state = MutableStateFlow(value = defaultState)
        scope.launch { observeMessages(scope = this, defaultState, startEffects) }
    }

    private suspend fun observeMessages(scope: CoroutineScope, defaultState: State, startEffects: Set<Effect>) {
        var currentState = defaultState
        _messages
            .onSubscription { params.effectHandlers.forEach { it.process(startEffects, scope, ::syncDispatch) } }
            .collect { msg ->
                val (newState, effects) = params.update(msg, currentState)
                currentState = newState
                _state.emit(newState)
                params.effectHandlers.forEach { it.process(effects, scope, ::syncDispatch) }
            }
    }

    override infix fun dispatch(msg: Msg) {
        scope.launch { syncDispatch(msg) }
    }

    override suspend infix fun syncDispatch(msg: Msg) {
        _messages.emit(msg)
    }
}
