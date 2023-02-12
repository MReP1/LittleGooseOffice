package little.goose.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class MultipleChoseHandler<T> {

    var isMultipleChose = MutableStateFlow(false)
    val itemList = ArrayList<T>()
    var needRefresh = false

    fun clickItem(item: T): Boolean {
        return if (itemList.contains(item)) {
            !removeItem(item)
        } else {
            addItem(item)
        }
    }

    fun release() {
        isMultipleChose.value = false
        clearItemList()
    }

    fun ready() {
        isMultipleChose.value = true
    }

    fun addList(item: List<T>) {
        itemList.addAll(item)
    }

    private fun addItem(item: T) = itemList.add(item)

    private fun removeItem(item: T) = itemList.remove(item)

    fun clearItemList() {
        itemList.clear()
    }

    suspend fun deleteItemList(action: suspend (List<T>) -> Unit) {
        withContext(Dispatchers.IO) {
            action(itemList.toList())
            release()
        }
    }

    //如果在多选情况下切页面，就要更新标志位，切回来的时候根据标志位判断是否需要刷新界面
    fun setNeedRefresh() {
        needRefresh = if (isMultipleChose.value) {
            release()
            true
        } else {
            false
        }
    }
}