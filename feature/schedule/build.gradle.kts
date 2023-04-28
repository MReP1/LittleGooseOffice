@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("goose.android.library")
    id("goose.android.compose")
    id("goose.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "little.goose.schedule"
}

dependencies {
    implementation(libs.androidx.appcompat)

    implementation(project(":core:design-system"))
    implementation(project(":core:common"))

    // Room database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // hilt
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.activity.ktx)
}