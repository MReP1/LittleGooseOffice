plugins {
    id("goose.android.library")
}

android {
    namespace = "little.goose.account.utils"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}