package io.github.pavelannin.keemun.plugin.template.core

data class CodeBlock(val import: List<String>, val code: String) {
    constructor(code: String) : this(import = emptyList(), code)
}

fun List<CodeBlock>.combine(): CodeBlock = CodeBlock(
    import = flatMap(CodeBlock::import),
    code = map(CodeBlock::code).joinToString(separator = "\n\n"),
)

fun CodeBlock.fileString(packageName: String): String = buildString {
    appendLine("package $packageName")
    appendLine()

    import.sorted()
        .toSet()
        .takeIf(Set<String>::isNotEmpty)
        ?.joinToString(separator = "\n") { "import $it" }
        ?.let { imports ->
            appendLine(imports)
            appendLine()
        }

    appendLine(code)
    appendLine()
}
