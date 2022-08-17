package little.goose.account.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

inline fun Fragment.launchAndRepeatWithViewLifeCycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
): Job {
    return viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            block()
        }
    }
}

inline fun <T> Flow<T>.collectWithLifecycleOwner(
    lifecycleOwner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.(data: T) -> Unit
) = this.collectWithLifecycle(lifecycleOwner.lifecycle, minActiveState, block)

inline fun <T> Flow<T>.collectWithFragment(
    fragment: Fragment,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.(data: T) -> Unit
): Job = this.collectWithLifecycle(fragment.viewLifecycleOwner.lifecycle, minActiveState, block)

inline fun <T> Flow<T>.collectWithLifecycle(
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.(data: T) -> Unit
) = lifecycle.coroutineScope.launch {
    this@collectWithLifecycle.flowWithLifecycle(lifecycle, minActiveState).collect {
        block(it)
    }
}