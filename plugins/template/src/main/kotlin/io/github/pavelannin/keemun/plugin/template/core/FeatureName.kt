package io.github.pavelannin.keemun.plugin.template.core

@JvmInline value class FeatureName(internal val value: String)

val FeatureName.functionPrefix: String get() = value.trim()
    .split(" ")
    .map { part -> part.replaceFirstChar { it.uppercase() } }
    .joinToString { it }
    .replaceFirstChar { it.lowercase() }

val FeatureName.classPrefix: String get() = value.trim()
    .split(" ")
    .map { part -> part.replaceFirstChar { it.uppercase() } }
    .joinToString { it }
    .replaceFirstChar { it.uppercase() }

val FeatureName.dir: String get() = value.trim()
    .split(" ")
    .map { part -> part.lowercase() }
    .joinToString { it }
