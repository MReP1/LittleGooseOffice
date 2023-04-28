package little.goose.office

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import little.goose.common.utils.initial
import little.goose.home.utils.homeDataStore
import javax.inject.Inject

@HiltAndroidApp
class AccountApplication : Application() {

    @Inject
    lateinit var appScope: CoroutineScope

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

@Volatile
var isAppInit = false