package little.goose.account.ui.memorial

import little.goose.account.logic.MemorialRepository
import little.goose.account.logic.data.entities.Memorial

object MemorialHelper {
    var memorialList: List<Memorial> = emptyList()
    var topMemorial: Memorial? = null

    suspend fun initMemorials() {
        memorialList = MemorialRepository.getAllMemorial()
    }

    suspend fun initTopMemorial() {
        topMemorial = MemorialRepository.getMemorialAtTop().firstOrNull()
    }
}