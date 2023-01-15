plugins {
    id("goose.android.library")
}

android {
    namespace = "little.goose.account.richtext"
}

dependencies {
    implementation(Dependencies.Androidx.core_ktx)
    implementation(Dependencies.Androidx.appcompat)
}