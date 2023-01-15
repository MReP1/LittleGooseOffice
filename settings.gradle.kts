includeBuild("build-logic")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }

    versionCatalogs {
        create("libs") {
            from(files("build-logic/gradle/libraries.versions.toml"))
        }
    }
}

rootProject.name = "account"
include(":app")
include(":RichText")
include(":calendarview")