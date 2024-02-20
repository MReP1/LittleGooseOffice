package plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class KmpComposePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            with(pluginManager) {
                apply(libs.findPlugin("jetbrains-compose").get().get().pluginId)
            }
            applyComposeStrongSkippingMode()
        }
    }

}