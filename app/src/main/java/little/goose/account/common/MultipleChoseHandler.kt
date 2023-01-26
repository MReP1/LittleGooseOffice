package little.goose.account.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import little.goose.account.logic.AccountRepository
import little.goose.account.logic.MemorialRepository
import little.goose.account.logic.NoteRepository
import little.goose.account.logic.ScheduleRepository
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.logic.data.entities.Note
import little.goose.account.logic.data.entities.Schedule
import little.goose.account.logic.data.entities.Transaction
import little.goose.account.utils.toTypeOr

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

    suspend fun deleteItemList(): List<T> = withContext(Dispatchers.IO) {
        if (itemList.isNotEmpty()) {
            var tempList = emptyList<T>()
            when (itemList[0]) {
                is Schedule -> {
                    tempList = itemList.toList()
                    ScheduleRepository.deleteSchedules(
                        tempList.toTypeOr(emptyList())
                    )
                }
                is Transaction -> {
                    tempList = itemList.toList()
                    AccountRepository.deleteTransactionList(
                        tempList.toTypeOr(emptyList())
                    )
                }
                is Note -> {
                    tempList = itemList.toList()
                    NoteRepository.deleteNoteList(
                        tempList.toTypeOr(emptyList())
                    )
                }
                is Memorial -> {
                    tempList = itemList.toList()
                    MemorialRepository.deleteMemorials(
                        tempList.toTypeOr(emptyList())
                    )
                }
            }
            release()
            tempList
        } else {
            emptyList()
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