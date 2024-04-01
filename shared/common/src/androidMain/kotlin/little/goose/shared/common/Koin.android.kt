package little.goose.shared.common

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.definition.Definition
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

actual inline fun <reified T : ViewModel> Module.commonViewModelFactory(
    qualifier: Qualifier?,
    noinline definition: Definition<T>,
) {
    viewModel<T>(qualifier, definition)
}

@Composable
actual inline fun <reified T : ViewModel> commonKoinViewModel(
    qualifier: Qualifier?,
    noinline parameters: ParametersDefinition?
): T {
    return koinViewModel(qualifier = qualifier, parameters = parameters)
}