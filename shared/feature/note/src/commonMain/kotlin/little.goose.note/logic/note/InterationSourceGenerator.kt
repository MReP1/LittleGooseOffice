@file:Suppress("FunctionName")

package little.goose.note.logic.note

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Create InteractionSource and observe it's focus status.
 */
internal fun InteractionSourceGetter(
    coroutineScope: CoroutineScope,
    mutableInteractionSourceMap: MutableMap<Long, MutableInteractionSource>,
    collectFocusJobMap: MutableMap<Long, Job>,
    getFocusingId: () -> Long?,
    updateFocusingId: (Long?) -> Unit
): (Long) -> MutableInteractionSource = { blockId ->
    mutableInteractionSourceMap.getOrPut(blockId) {
        MutableInteractionSource().also { mis ->
            collectFocusJobMap[blockId]?.cancel()
            collectFocusJobMap[blockId] = coroutineScope.launch {
                mis.interactions.collect { interaction ->
                    when (interaction) {
                        is FocusInteraction.Focus -> updateFocusingId(blockId)
                        is FocusInteraction.Unfocus -> {
                            if (blockId == getFocusingId()) {
                                updateFocusingId(null)
                            }
                        }
                    }
                }
            }
        }
    }
}