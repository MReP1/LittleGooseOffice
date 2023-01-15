package plugin

import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandlerScope.implementationDefaultTestDependencies() {
    "testImplementation"("junit:junit:4.13.2")
    "androidTestImplementation"("androidx.test.ext:junit:1.1.4")
    "androidTestImplementation"("androidx.test.espresso:espresso-core:3.5.0")
}