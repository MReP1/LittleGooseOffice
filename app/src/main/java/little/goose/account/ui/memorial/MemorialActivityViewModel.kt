package little.goose.account.ui.memorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import little.goose.account.logic.MemorialRepository
import little.goose.account.logic.data.constant.TYPE_ADD
import little.goose.account.logic.data.constant.TYPE_MODIFY
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.appScope
import java.util.*

class MemorialActivityViewModel : ViewModel() {
    var type = TYPE_ADD
    var memorial: Memorial = Memorial(null, "纪念日", false, Date())
    val content = MutableStateFlow("纪念日")
    val time = MutableStateFlow(Date())

    var isChangeTop = false

    init {
        viewModelScope.launch { time.collect { memorial.time = it } }
        viewModelScope.launch { content.collect { memorial.content = it } }
    }

    fun storeMemorial() {
        if (isChangeTop && memorial.isTop) { //在主线程判断，以免线程不同步
            appScope.launch {
                val topList = MemorialRepository.getMemorialAtTopFlow().first()
                for (memorial in topList) {
                    memorial.isTop = false
                }
                launch { MemorialRepository.updateMemorials(topList) }

                launch {
                    when (type) {
                        TYPE_MODIFY -> MemorialRepository.updateMemorial(memorial)
                        TYPE_ADD -> MemorialRepository.addMemorial(memorial)
                    }
                }
            }
        } else {
            appScope.launch {
                when (type) {
                    TYPE_MODIFY -> MemorialRepository.updateMemorial(memorial)
                    TYPE_ADD -> MemorialRepository.addMemorial(memorial)
                }
            }
        }

    }

}