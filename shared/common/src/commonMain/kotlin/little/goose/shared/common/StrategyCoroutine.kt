package little.goose.shared.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

sealed interface LaunchStrategy : (CoroutineScope, CoroutineContext, suspend () -> Unit) -> Unit {

    class CancelPrevious : LaunchStrategy {
        private var job: Job? = null
        override fun invoke(scope: CoroutineScope, context: CoroutineContext, action: suspend () -> Unit) {
            job?.cancel()
            job = scope.launch(context) { action() }
        }
    }

    class DropLast : LaunchStrategy {
        private var job: Job? = null
        override fun invoke(scope: CoroutineScope, context: CoroutineContext, action: suspend () -> Unit) {
            val isRunning = job?.takeUnless(Job::isCompleted) != null
            if (!isRunning) {
                job = scope.launch(context) { action() }
            }
        }
    }

    class Suspend(private val mutex: Mutex = Mutex()) : LaunchStrategy {
        override fun invoke(scope: CoroutineScope, context: CoroutineContext, action: suspend () -> Unit) {
            scope.launch(context) {
                mutex.withLock { action() }
            }
        }
    }

}

fun CoroutineScope.strategyJob(
    strategy: LaunchStrategy,
    context: CoroutineContext = EmptyCoroutineContext,
    action: suspend () -> Unit
): () -> Unit = fun() { strategy.invoke(this, context, action) }