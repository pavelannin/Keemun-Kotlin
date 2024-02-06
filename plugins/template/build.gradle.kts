plugins {
    alias(deps.plugins.kotlin.jvm)
    alias(deps.plugins.intellij)
}

group = "io.github.pavelannin.keemun.template"
version = "1.0.0"

// Required as the "intellij" plugin is overriding the repositories from "settings.gradle"
repositories {
    google()
    mavenCentral()
}

intellij {
    pluginName = "Keemun Template"
    version = "2023.1.1"
    type = "IC"
    plugins.set(listOf("android"))
}
