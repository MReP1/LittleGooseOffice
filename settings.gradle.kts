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
}

rootProject.name = "account"
include(":app")
include(":RichText")
include(":calendarview")
include(":core:design-system")
include(":core:common")
include(":feature:memorial")
include(":feature:schedule")
include(":feature:note")
