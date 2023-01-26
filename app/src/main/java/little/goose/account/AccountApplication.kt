package little.goose.account

import android.app.Application
import android.content.Context
import kotlinx.coroutines.*
import little.goose.account.ui.account.transaction.TransactionHelper
import little.goose.account.utils.DataStoreHelper

class AccountApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        initData()
    }

    //初始化数据 防止第一次打开还要加载
    private fun initData() {
        appScope.launch {
            awaitAll(
                async { TransactionHelper.initTransaction() },
                async { DataStoreHelper.INSTANCE.initDataStore() }
            )
            isAppInit = true
        }
    }
}

val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
lateinit var appContext: Context

@Volatile
var isAppInit = false