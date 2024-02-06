package io.github.pavelannin.keemun.plugin.template.templates.feature

import io.github.pavelannin.keemun.plugin.template.core.CodeBlock

fun FeatureTemplate.viewStateTransform() = CodeBlock(
    import = listOf("io.github.pavelannin.keemun.core.StateTransform"),
    code = """
        internal fun $viewStateTransformName() = StateTransform<$stateName, $viewStateName> { state ->
            $viewStateName(
                data = state.data,
            )
        }
    """.trimIndent()
)

fun FeatureTemplate.viewState(): CodeBlock = if (isUiComposeNeed) {
    CodeBlock(
        import = listOf("androidx.compose.runtime.Immutable"),
        code = """
            @Immutable
            internal data class $viewStateName(
                val data: String,
            )
        """.trimIndent()
    )
} else {
    CodeBlock(
        code = """
            internal data class $viewStateName(
                val data: String,
            )
        """.trimIndent()
    )
}
