package io.github.pavelannin.keemun.plugin.template.templates.feature

import io.github.pavelannin.keemun.plugin.template.core.CodeBlock

fun FeatureTemplate.update(): CodeBlock = when (msgStructure) {
    FeatureTemplate.MsgStructure.Unified ->
        CodeBlock(
            import = listOf(
                "io.github.pavelannin.keemun.core.Update"
            ),
            code = """
                internal val $updateName = Update<$stateName, $msgName, $effectName> { msg, state ->
                    state to emptySet()
                }
            """.trimIndent()
        )

    FeatureTemplate.MsgStructure.Distributed ->
        CodeBlock(
            import = listOf(
                "io.github.pavelannin.keemun.core.Update"
            ),
            code = """
                internal val $externalUpdateName = Update<$stateName, $externalMsgName, $effectName> { msg, state ->
                    state to emptySet()
                }
                
                internal val $internalUpdateName = Update<$stateName, $internalMsgName, $effectName> { msg, state ->
                    state to emptySet()
                }
            """.trimIndent()
        )
}

fun FeatureTemplate.msg(): CodeBlock = when (msgStructure) {
    FeatureTemplate.MsgStructure.Unified ->
        CodeBlock(
            code = """
                internal sealed class $msgName {
                    
                }
            """.trimIndent()
        )

    FeatureTemplate.MsgStructure.Distributed ->
        CodeBlock(
            code = """
                internal sealed interface $msgName
                
                internal sealed class $externalMsgName : $msgName {
                    
                }
                
                internal sealed class $internalMsgName : $msgName {
                    
                }
            """.trimIndent()
        )
}
