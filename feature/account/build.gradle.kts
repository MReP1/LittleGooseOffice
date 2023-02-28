@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("goose.android.library")
    id("goose.android.compose")
    id("goose.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "little.goose.account"
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)

    implementation(libs.androidx.localbroadcastManager)
    implementation(libs.androidx.constraintLayout)

    implementation(project(":core:design-system"))
    implementation(project(":core:common"))

    implementation(libs.androidx.fragment.ktx)
    implementation(libs.google.android.material)

    implementation(libs.androidx.recyclerView)

    // Room database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.constraintLayout.compose)
    implementation(libs.accompanist.pager)

    //MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}