package io.github.pavelannin.keemun.core.consistent

import io.github.pavelannin.keemun.core.collectAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ConsistentTest {

    @Test
    fun testUpdater1() {
        val userId = 101
        val defaultState = ConsistentState(progress = false, loadedUser = null)
        val msg = ConsistentMsg.LoadUserById(id = userId)
        val (state, effect) = update(msg, defaultState)
        assertEquals(actual = state, expected = ConsistentState(progress = true, loadedUser = null))
        assertEquals(actual = effect, expected = setOf(ConsistentEffect.LoadUser(userId)))
    }

    @Test
    fun testUpdater2() {
        val defaultState = ConsistentState(progress = true, loadedUser = null)
        val user = ConsistentState.User(id = 101)
        val msg = ConsistentMsg.UserWasLoaded(user)
        val (state, effect) = update(msg, defaultState)
        assertEquals(actual = state, expected = ConsistentState(progress = false, loadedUser = user))
        assertEquals(actual = effect, expected = emptySet())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun full() = runTest(UnconfinedTestDispatcher()) {
        val scope = CoroutineScope(Dispatchers.Default)
        val store = store(scope)
        val statesListDef = store.state.collectAsync(scope)
        val userId = 101
        val expected = listOf(
            ConsistentState(progress = false, loadedUser = null), // Initial state
            ConsistentState(progress = true, loadedUser = null),
            ConsistentState(progress = false, loadedUser = ConsistentState.User(id = userId))
        )
        store.syncDispatch(ConsistentMsg.LoadUserById(id = userId))
        delay(timeMillis = 300)
        try {
            assertEquals(actual = statesListDef.await(), expected = expected)
        } finally {
            scope.cancel()
        }
    }
}
