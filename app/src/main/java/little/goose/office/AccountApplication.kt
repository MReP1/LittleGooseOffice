package little.goose.office

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*
import little.goose.account.ui.transaction.TransactionHelper
import little.goose.office.utils.DataStoreHelper
import little.goose.common.utils.DateTimeUtils
import little.goose.common.utils.KeyBoard
import little.goose.common.utils.UIUtils

@HiltAndroidApp
class AccountApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        DateTimeUtils.appContext = this
        UIUtils.appContext = this
        KeyBoard.appContext = this


        initData()
    }

    //初始化数据 防止第一次打开还要加载
    private fun initData() {
        appScope.launch {
            awaitAll(
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