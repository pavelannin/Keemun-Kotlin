package io.github.pavelannin.keemun.sample.counter.features.counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.pavelannin.keemun.core.store.Store

@Composable
internal fun Counter(store: Store<CounterViewState, ExternalMsg>) {
    val state = store.state.collectAsState()
    Main(
        state = state.value,
        onSyncIncrementClick = { store dispatch ExternalMsg.IncrementSync },
        onSyncDecrementClick = { store dispatch ExternalMsg.DecrementSync },
        onAsyncIncrementClick = { store dispatch ExternalMsg.IncrementAsync },
        onAsyncDecrementClick = { store dispatch ExternalMsg.DecrementAsync },
    )
}

@Composable
private fun Main(
    state: CounterViewState,
    onSyncIncrementClick: () -> Unit,
    onSyncDecrementClick: () -> Unit,
    onAsyncIncrementClick: () -> Unit,
    onAsyncDecrementClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Counter(
            modifier = Modifier.weight(1f),
            title = "Sync",
            value = state.syncCount,
            isLoading = false,
            onIncrementClick = onSyncIncrementClick,
            onDecrementClick = onSyncDecrementClick,
        )
        Counter(
            modifier = Modifier.weight(1f),
            title = "Async",
            value = state.asyncCount,
            isLoading = state.isAsyncRunning,
            onIncrementClick = onAsyncIncrementClick,
            onDecrementClick = onAsyncDecrementClick,
        )
    }
}

@Composable
private fun Counter(
    title: String,
    value: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onIncrementClick: () -> Unit,
    onDecrementClick: () -> Unit,
) {
    Column(

        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            onClick = onIncrementClick,
        ) { Text(text = "+") }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = value, fontSize = 18.sp)
            androidx.compose.animation.AnimatedVisibility(visible = isLoading) {
                CircularProgressIndicator()
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            onClick = onDecrementClick,
        ) { Text(text = "-") }
    }
}
