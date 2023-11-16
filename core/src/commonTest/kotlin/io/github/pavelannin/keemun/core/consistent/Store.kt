package io.github.pavelannin.keemun.core.consistent

import io.github.pavelannin.keemun.core.Update
import io.github.pavelannin.keemun.core.store.Store
import io.github.pavelannin.keemun.core.store.StoreParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private suspend fun loadUserById(id: Int)  = withContext(Dispatchers.Default) { ConsistentState.User(id) }

data class ConsistentState(val progress: Boolean, val loadedUser: User?) {
    data class User(val id: Int)
}

sealed class ConsistentMsg {
    data class LoadUserById(val id: Int) : ConsistentMsg()
    data class UserWasLoaded(val user: ConsistentState.User) : ConsistentMsg()
}

sealed class ConsistentEffect {
    data class LoadUser(val id: Int) : ConsistentEffect()
}

val update = Update<ConsistentState, ConsistentMsg, ConsistentEffect> { msg, model ->
    when (msg) {
        is ConsistentMsg.LoadUserById ->
            model.copy(progress = true) to setOf(ConsistentEffect.LoadUser(msg.id))

        is ConsistentMsg.UserWasLoaded ->
            model.copy(progress = false, loadedUser = msg.user) to emptySet()
    }
}

fun store(scope: CoroutineScope) = Store(
    previousState = null,
    coroutineScope = scope,
    params = StoreParams(
        init = { previous -> (previous ?: ConsistentState(progress = false, loadedUser = null)) to emptySet() },
        update = update,
        effectHandler = { eff, dispatch ->
            when (eff) {
                is ConsistentEffect.LoadUser -> {
                    val user = loadUserById(eff.id)
                    dispatch(ConsistentMsg.UserWasLoaded(user))
                }
            }
        }
    )
)
