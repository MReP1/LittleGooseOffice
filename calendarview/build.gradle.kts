plugins {
    id("goose.android.library")
}

android {

    lint {
        abortOnError = false
    }

//    tasks.register("intoJar", Copy::class.java) {
//        delete("build/libs/CalendarView.jar")
//        from("build/intermediates/bundles/release/")
//        into("build/libs/")
//        include("classes.jar")
//        rename("classes.jar", "CalendarView.jar")
//    }.apply {
//        dependsOn("build")
//    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to "*.jar")))
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.0") {
        exclude(group = "com.android.support", module = "support-annotations")
    }

    implementation(Dependencies.Androidx.appcompat)
    implementation(Dependencies.recycler_view)
    testImplementation("junit:junit:4.13.2")
}