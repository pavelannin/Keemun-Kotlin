package io.github.pavelannin.keemun.core.completeness

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

class CompletenessTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun run() = runTest(UnconfinedTestDispatcher()) {
        val dispatchCount = 50
        val expected = CompletenessState(
            flow1 = dispatchCount,
            flow2 = dispatchCount,
            flow3 = dispatchCount,
        )

        val scope = CoroutineScope(Dispatchers.Default)
        val store = store(scope, dispatchCount)

        val statesListDef = store.state.collectAsync(scope)
        delay(timeMillis = 300)

        try {
            assertEquals(actual = statesListDef.await().last(), expected = expected)
        } finally {
            scope.cancel()
        }
    }
}
