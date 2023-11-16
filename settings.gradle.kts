pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

@Suppress(names = ["UnstableApiUsage"])
dependencyResolutionManagement {
    versionCatalogs {
        create("deps") {
            from(files("dependencies.versions.toml"))
        }
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Keemun"

include(
    ":core",
    ":decompose",
)
