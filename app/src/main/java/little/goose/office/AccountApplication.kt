package little.goose.office

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
            isAppInit = true
        }
    }
}

@Volatile
var isAppInit = false