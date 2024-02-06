includeBuild("build-logic")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
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
include(":design-catalog")
include(":shared:chart")
include(":shared:common")
include(":note")
include(":shared:data:note")
include(":shared:data:database")
include(":shared:feature:note")