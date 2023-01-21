plugins {
    id("goose.android.library")
}

android {

    namespace = "com.haibin.calendarview"

    lint {
        abortOnError = false
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerView)
}