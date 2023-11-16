plugins {
    alias(deps.plugins.kotlin.multiplatform)
    alias(deps.plugins.kotlin.cocoapods)
    alias(deps.plugins.android.library)
}

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "shared"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(deps.kotlin.std.common)
            api(deps.kotlin.coroutines.core)
        }
        commonTest.dependencies {
            implementation(deps.kotlin.test)
            implementation(deps.kotlin.coroutines.test)
        }
    }
}

android {
    namespace = "io.github.pavelannin.keemun.core"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}
