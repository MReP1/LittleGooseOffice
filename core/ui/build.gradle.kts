plugins {
    id("goose.android.library")
    id("goose.android.compose")
}

android {
    namespace = "little.goose.ui"
}

dependencies {

    implementation(project(":core:common"))
    implementation(project(":core:design-system"))
}