plugins {
    alias(deps.plugins.kotlin.multiplatform)
    alias(deps.plugins.kotlin.serialization)
    alias(deps.plugins.kotlin.cocoapods)
    alias(deps.plugins.android.library)
    alias(deps.plugins.kotlin.compose)
}

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    androidTarget { compilations.all { kotlinOptions.jvmTarget = "1.8" } }

    cocoapods {
        name = "FeatureCounter"
        version = "1.1.0"
        summary = "Multiplatform sample feature"
        homepage = "https://github.com/pavelannin/Keemun-Kotlin"
        ios.deploymentTarget = "13.0"
        podfile = project.file("../ios/Podfile")
        framework {
            baseName = "FeatureCounter"
            export(deps.keemun.swiftui)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
//            api(deps.keemun.core)
            api(deps.kotlin.serialization)
            implementation(deps.kotlin.std.common)
        }
        androidMain.dependencies {
            api(project(":connectors:decompose"))
//            api(deps.keemun.decompose)
            api(project.dependencies.platform(deps.androidX.compose.bom))
            api(deps.bundles.androidX.compose)
        }
        iosMain.dependencies {
            implementation(project(":connectors:swift-ui"))
//            implementation(deps.keemun.swiftui)
        }
    }
}

android {
    namespace = "io.github.pavelannin.keemun.sample.counter"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }

    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = "1.5.15"
}
