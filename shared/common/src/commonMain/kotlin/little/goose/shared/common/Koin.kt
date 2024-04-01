package little.goose.shared.common

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import org.koin.core.definition.Definition
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

@Composable
expect inline fun <reified T : ViewModel> commonKoinViewModel(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T

expect inline fun <reified T : ViewModel> Module.commonViewModelFactory(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>,
)