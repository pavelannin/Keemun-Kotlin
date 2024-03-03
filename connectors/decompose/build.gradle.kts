import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(deps.plugins.kotlin.multiplatform)
    alias(deps.plugins.android.library)
    alias(deps.plugins.maven.publish)
}

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    androidTarget {
        publishAllLibraryVariants()
        compilations.all { kotlinOptions.jvmTarget = "1.8" }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
            api(deps.decompose.core)
            implementation(deps.kotlin.std.common)
        }
    }
}

android {
    namespace = "io.github.pavelannin.keemun.decompose"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
}

mavenPublishing {
    val artifactId = "keemun-decompose"
    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()
    coordinates("io.github.pavelannin", artifactId, "1.2.0")
    pom {
        name.set(artifactId)
        description.set("Keemun is a multiplatform Kotlin framework that provides a way to write shared code using The Elm Architecture pattern.")
        url.set("https://github.com/pavelannin/Keemun-Kotlin")
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                name.set("Pavel Annin")
                email.set("pavelannin.dev@gmail.com")
            }
        }
        scm {
            connection.set("scm:git:github.com/pavelannin/Keemun-Kotlin.git")
            developerConnection.set("scm:git:ssh://github.com/pavelannin/Keemun-Kotlin.git")
            url.set("https://github.com/pavelannin/Keemun-Kotlin/tree/main")
        }
    }
}
