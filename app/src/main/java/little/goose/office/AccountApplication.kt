package little.goose.office

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import little.goose.home.utils.homeDataStore
import little.goose.home.utils.initial

@HiltAndroidApp
class AccountApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initData()
    }

    //初始化数据 防止第一次打开还要加载
    private fun initData() {
        appScope.launch {
            homeDataStore.initial()
            isAppInit = true
        }
    }
}

val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

@Volatile
var isAppInit = false