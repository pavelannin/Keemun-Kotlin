plugins {
    alias(deps.plugins.android.application) apply  false
    alias(deps.plugins.android.library) apply  false
    alias(deps.plugins.kotlin.multiplatform) apply  false
    alias(deps.plugins.kotlin.cocoapods) apply  false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
