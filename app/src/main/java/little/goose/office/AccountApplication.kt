package little.goose.office

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*
import little.goose.home.utils.DataStoreHelper

@HiltAndroidApp
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
                async { DataStoreHelper.INSTANCE.initDataStore(this@AccountApplication) }
            )
            isAppInit = true
        }
    }
}

val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
lateinit var appContext: Context

@Volatile
var isAppInit = false