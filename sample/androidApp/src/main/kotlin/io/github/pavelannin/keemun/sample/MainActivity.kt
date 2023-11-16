package io.github.pavelannin.keemun.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.defaultComponentContext
import io.github.pavelannin.keemun.sample.features.counter.CounterViewState
import io.github.pavelannin.keemun.sample.features.counter.ExternalMsg
import io.github.pavelannin.keemun.sample.features.counter.counterFeature

class MainActivity : ComponentActivity() {

    private val feature by lazy { counterFeature(defaultComponentContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val state = feature.viewState.collectAsState()
                Main(
                    state = state.value,
                    onSyncIncrementClick = { feature dispatch ExternalMsg.IncrementSync },
                    onSyncDecrementClick = { feature dispatch ExternalMsg.DecrementSync },
                    onAsyncIncrementClick = { feature dispatch ExternalMsg.IncrementAsync },
                    onAsyncDecrementClick = { feature dispatch ExternalMsg.DecrementAsync },
                )
            }
        }
    }
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
