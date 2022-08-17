package little.goose.account

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import kotlinx.coroutines.*
import little.goose.account.ui.account.transaction.TransactionHelper
import little.goose.account.ui.account.transaction.icon.TransactionIconHelper
import little.goose.account.ui.memorial.MemorialHelper
import little.goose.account.ui.notebook.note.NoteHelper
import little.goose.account.ui.schedule.ScheduleHelper
import little.goose.account.utils.DataStoreHelper

class AccountApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        val supervisorScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        initData()
    }

    //初始化数据 防止第一次打开还要加载
    private fun initData() {
        supervisorScope.launch {
            val initIconDataDeferred = async { TransactionIconHelper.initIconData() }
            val initTransactionDeferred = async { TransactionHelper.initTransaction() }
            val initScheduleDeferred = async { ScheduleHelper.initSchedule() }
            val initNoteDeferred = async { NoteHelper.initNote() }
            val initMemorialsDeferred = async { MemorialHelper.initMemorials() }
            val initTopMemorialDeferred = async { MemorialHelper.initTopMemorial() }
            val initDataStoreDeferred = async { DataStoreHelper.INSTANCE.initDataStore() }
            initIconDataDeferred.await()
            initTransactionDeferred.await()
            initScheduleDeferred.await()
            initNoteDeferred.await()
            initMemorialsDeferred.await()
            initTopMemorialDeferred.await()
            initDataStoreDeferred.await()
            isAppInit = true
        }
    }
}

var isAppInit = false
val superScope get() = AccountApplication.supervisorScope
val appContext get() = AccountApplication.context