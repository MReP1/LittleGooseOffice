package little.goose.office

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import little.goose.common.di.AppCoroutineScope
import little.goose.home.logic.HomePageDataHolder
import javax.inject.Inject

@HiltAndroidApp
class AccountApplication : Application() {

    @Inject
    @AppCoroutineScope
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var holder: HomePageDataHolder

    override fun onCreate() {
        super.onCreate()
        initData()
    }

    //初始化数据 防止第一次打开还要加载
    private fun initData() {
        appScope.launch {
            holder.homePage.first { it != -1 }
            isAppInit = true
        }
    }
}

@Volatile
var isAppInit = false