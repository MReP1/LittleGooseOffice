package little.goose.common.di

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.BINARY

@Qualifier
@Retention(BINARY)
annotation class Dispatcher(val dispatcher: GooseDispatchers)

enum class GooseDispatchers {
    Default, IO;
}
