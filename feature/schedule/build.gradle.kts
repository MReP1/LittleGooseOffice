plugins {
    id("goose.android.library")
    id("goose.android.compose")
}

android {
    namespace = "little.goose.schedule"
}

dependencies {
    implementation(libs.androidx.appcompat)

    
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
}