package plugin

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

fun CommonExtension<*, *, *, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}

inline fun <reified T> Project.applyAndroid(block: T.() -> Unit) {
    (extensions.getByName("android") as T).apply(block)
}

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun Project.applyComposeStrongSkippingMode() {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        val p = "-P"
        val strongSkippingPlugin =
            "plugin:androidx.compose.compiler.plugins.kotlin:experimentalStrongSkipping=true"
        val freeArgList = compilerOptions.freeCompilerArgs.get()
        val isContainsStrongSkipping =
            freeArgList.contains(p) && freeArgList.contains(strongSkippingPlugin)
        if (!isContainsStrongSkipping) {
            compilerOptions.freeCompilerArgs.addAll(p, strongSkippingPlugin)
        }
    }
}