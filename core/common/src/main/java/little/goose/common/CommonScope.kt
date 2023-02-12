package little.goose.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

//FIXME
val commonScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())