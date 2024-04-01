package little.goose.shared.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import org.koin.compose.currentKoinScope
import org.koin.core.definition.Definition
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

actual inline fun <reified T : ViewModel> Module.commonViewModelFactory(
    qualifier: Qualifier?,
    noinline definition: Definition<T>,
) {
    factory(qualifier, definition)
}

@Composable
actual inline fun <reified T : ViewModel> commonKoinViewModel(
    qualifier: Qualifier?,
    noinline parameters: ParametersDefinition?
): T {
    val koin = currentKoinScope()
    val viewModel = remember<T>(koin) {
        koin.get(qualifier = qualifier, parameters = parameters)
    }
    // Fixme clear ViewModel
    return viewModel
}