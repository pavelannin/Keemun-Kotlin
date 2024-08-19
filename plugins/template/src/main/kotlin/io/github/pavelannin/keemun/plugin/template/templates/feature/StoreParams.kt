package io.github.pavelannin.keemun.plugin.template.templates.feature

import io.github.pavelannin.keemun.plugin.template.core.CodeBlock

fun FeatureTemplate.storeParams(): CodeBlock = when (msgStructure) {
    FeatureTemplate.MsgStructure.Unified ->
        CodeBlock(
            import = listOf(
                "io.github.pavelannin.keemun.core.EffectHandler",
                "io.github.pavelannin.keemun.core.store.StoreParams",
            ),
            code = """
                internal fun $storeParamsName(
                    effectHandler: EffectHandler<$effectName, $msgName>,
                ) = StoreParams(
                    start = { savedState ->
                        val state = savedState ?: $stateName(
                            data = "",
                        )
                        state to ${if (isInputEventNeed) "setOf($effectName.ObserveInputEvents)" else "emptySet()"}
                    },
                    update = $updateName,
                    effectHandler = effectHandler,
                )
            """.trimIndent()
        )

    FeatureTemplate.MsgStructure.Distributed ->
        CodeBlock(
            import = listOf(
                "io.github.pavelannin.keemun.core.EffectHandler",
                "io.github.pavelannin.keemun.core.store.StoreParams",
            ),
            code = """
                internal fun $storeParamsName(
                    effectHandler: EffectHandler<$effectName, $internalMsgName>,
                ) = StoreParams(
                    start = { savedState ->
                        val state = savedState ?: $stateName(
                            data = "",
                        )
                        state to ${if (isInputEventNeed) "setOf($effectName.ObserveInputEvents)" else "emptySet()"}
                    },
                    externalUpdate = $externalUpdateName,
                    internalUpdate = $internalUpdateName,
                    effectHandler = effectHandler,
                )
            """.trimIndent()
        )
}

fun FeatureTemplate.state() = CodeBlock(
    import = listOf("kotlinx.serialization.Serializable"),
    code = """
        @Serializable
        internal data class $stateName(
            val data: String,
        )
    """.trimIndent()
)

fun FeatureTemplate.inputEvent() = CodeBlock(
    code = """
        sealed class $inputEventName {
            
        }
    """.trimIndent()
)

fun FeatureTemplate.outputEvent() = CodeBlock(
    code = """
        sealed class $outputEventName {
            
        }
    """.trimIndent()
)
