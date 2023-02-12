@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("goose.android.library")
    id("goose.android.compose")
    id("goose.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "little.goose.memorial"
}

dependencies {
    implementation(libs.androidx.appcompat)

    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.0.0")


    implementation(project(":core:design-system"))
    implementation(project(":core:common"))
    implementation(project(":core:utils"))

    implementation(libs.androidx.fragment.ktx)

    // Room database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.constraintLayout.compose)

}