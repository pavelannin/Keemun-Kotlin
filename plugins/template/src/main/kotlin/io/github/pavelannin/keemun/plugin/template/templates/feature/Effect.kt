package io.github.pavelannin.keemun.plugin.template.templates.feature

import io.github.pavelannin.keemun.plugin.template.core.CodeBlock

fun FeatureTemplate.effectHandler(): CodeBlock {
    val msgClassName = when (msgStructure) {
        FeatureTemplate.MsgStructure.Unified -> msgName
        FeatureTemplate.MsgStructure.Distributed -> internalMsgName
    }
    return when {
        isInputEventNeed && isOutputEventNeed ->
            CodeBlock(
                import = listOf(
                    "kotlinx.coroutines.flow.Flow",
                    "kotlinx.coroutines.flow.map",
                    "io.github.pavelannin.keemun.core.EffectHandler"
                ),
                code = """
                    internal fun $effectHandlerName(
                        input: Flow<$inputEventName>,
                        output: ($outputEventName) -> Unit,
                    ) = EffectHandler<$effectName, $msgClassName> { effect, dispatch ->
                        when (effect) {
                            is $effectName.ObserveInputEvents ->
                                input
                                    .map($inputEventName::toMsg)
                                    .collect { msg -> dispatch(msg) }
                            
                            is $effectName.SendOutputEvent ->
                                output(effect.event)
                        }                
                    }
                    
                    private fun $inputEventName.toMsg(): $msgClassName = when (this) {
                        
                    }
                """.trimIndent()
            )

        isInputEventNeed ->
            CodeBlock(
                import = listOf(
                    "kotlinx.coroutines.flow.Flow",
                    "kotlinx.coroutines.flow.map",
                    "io.github.pavelannin.keemun.core.EffectHandler"
                ),
                code = """
                    internal fun $effectHandlerName(
                        input: Flow<$inputEventName>,
                    ) = EffectHandler<$effectName, $msgClassName> { effect, dispatch ->
                        when (effect) {
                            is $effectName.ObserveInputEvents ->
                                input
                                    .map($inputEventName::toMsg)
                                    .collect { msg -> dispatch(msg) }

                        }                
                    }
                    
                    private fun $inputEventName.toMsg(): $msgClassName = when (this) {
                        
                    }
                """.trimIndent()
            )

        else ->
            CodeBlock(
                import = listOf(
                    "io.github.pavelannin.keemun.core.EffectHandler"
                ),
                code = """
                    internal fun $effectHandlerName() = EffectHandler<$effectName, $msgClassName> { effect, dispatch ->
                        when (effect) {
                            
                        }                
                    }
                """.trimIndent()
            )
    }
}

fun FeatureTemplate.effect(): CodeBlock = when {
    isInputEventNeed && isOutputEventNeed ->
        CodeBlock(
            code = """
                internal sealed class $effectName {
                    data object ObserveInputEvents : $effectName()
                    data class SendOutputEvent(val event: $outputEventName) : $effectName()
                }
            """.trimIndent()
        )

    isInputEventNeed ->
        CodeBlock(
            code = """
                internal sealed class $effectName {
                    data object ObserveInputEvents : $effectName()
                }
            """.trimIndent()
        )

    isOutputEventNeed ->
        CodeBlock(
            code = """
                internal sealed class $effectName {
                    data class SendOutputEvent(val event: $outputEventName) : $effectName()
                }
            """.trimIndent()
        )

    else ->
        CodeBlock(
            code = """
                internal sealed class $effectName {
                    
                }
            """.trimIndent()
        )
}
