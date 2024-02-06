package io.github.pavelannin.keemun.plugin.template.templates.feature

import io.github.pavelannin.keemun.plugin.template.core.CodeBlock

fun FeatureTemplate.ui(storePackageName: String): CodeBlock {
    val msgClassName = when (msgStructure) {
        FeatureTemplate.MsgStructure.Unified -> msgName
        FeatureTemplate.MsgStructure.Distributed -> externalMsgName
    }
    return CodeBlock(
        import = listOf(
            "androidx.compose.runtime.Composable",
            "androidx.compose.runtime.collectAsState",
            "androidx.compose.runtime.getValue",
            "io.github.pavelannin.keemun.core.store.Store",
            "$storePackageName.$msgClassName",
            "$storePackageName.$viewStateName",
        ),
        code = """
            @Composable
            internal fun $uiName(store: Store<$viewStateName, $msgClassName>) {
                val state by store.state.collectAsState()
            }
        """.trimIndent()
    )
}
