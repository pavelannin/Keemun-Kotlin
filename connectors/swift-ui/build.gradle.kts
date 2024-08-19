import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(deps.plugins.kotlin.multiplatform)
    alias(deps.plugins.maven.publish)
}

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
            implementation(deps.kotlin.std.common)
        }
    }

    kotlin.sourceSets.all {
        languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
        languageSettings.optIn("kotlin.experimental.ExperimentalObjCRefinement")
    }
}

mavenPublishing {
    val artifactId = "keemun-swiftui"
    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()
    coordinates("io.github.pavelannin", artifactId, "2.0.0")
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
