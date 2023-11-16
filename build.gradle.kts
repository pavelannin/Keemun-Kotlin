plugins {
    alias(deps.plugins.android.application) apply false
    alias(deps.plugins.android.library) apply false
    alias(deps.plugins.kotlin.multiplatform) apply false
    alias(deps.plugins.kotlin.android) apply false
    alias(deps.plugins.kotlin.cocoapods) apply false
    alias(deps.plugins.kotlin.serialization) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
