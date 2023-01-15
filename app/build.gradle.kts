@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("goose.android.app")
    id("goose.android.compose")
    alias(libs.plugins.ksp)
}

android {
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(Dependencies.Androidx.core_ktx)
    implementation(Dependencies.Androidx.appcompat)

    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    implementation("androidx.fragment:fragment-ktx:1.4.1")
    implementation("androidx.activity:activity-ktx:1.6.1")

    //SplashScreen
    implementation("androidx.core:core-splashscreen:1.0.0")

    val room_version = "2.4.2"
    //Room数据库
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    //协程
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    val lifecycle_version = "2.4.1"

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")//lifecycleScope
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")//viewModelScope
    implementation("androidx.lifecycle:lifecycle-process:$lifecycle_version")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4")

    //MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //富文本
    implementation(project(":RichText"))
    //自定义Calendar
    implementation(project(":calendarview"))

    //DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

}