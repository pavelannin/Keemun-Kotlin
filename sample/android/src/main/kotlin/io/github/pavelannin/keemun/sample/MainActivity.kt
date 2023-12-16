package io.github.pavelannin.keemun.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import com.arkivanov.decompose.defaultComponentContext
import io.github.pavelannin.keemun.sample.counter.CounterFeatureScope

class MainActivity : ComponentActivity() {

    private val counterFeatureScope = CounterFeatureScope()
    private val counterConnector by lazy { counterFeatureScope.counter(defaultComponentContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme { counterFeatureScope.CounterRender(store = counterConnector) }
        }
    }
}
