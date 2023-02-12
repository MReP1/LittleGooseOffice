plugins {
    id("goose.android.library")
}

android {
    namespace = "little.goose.account.utils"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}