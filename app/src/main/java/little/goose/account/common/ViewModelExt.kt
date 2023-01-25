package little.goose.account.common

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
inline fun <reified VM : ViewModel> viewModelInstance(crossinline create: () -> VM): VM =
    viewModel(factory = object : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return create() as T
        }
    })