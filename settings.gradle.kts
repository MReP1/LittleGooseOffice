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
include(":core:design-system")
include(":core:common")
include(":core:ui")
include(":feature:memorial")
include(":feature:note")
include(":feature:account")
include(":feature:home")
include(":feature:search")
include(":appwidget")
include(":feature:settings")