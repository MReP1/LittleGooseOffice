package little.goose.account.ui

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import little.goose.account.R
import little.goose.account.databinding.ActivityMainBinding
import little.goose.account.isAppInit
import little.goose.account.logic.data.constant.*
import little.goose.account.superScope
import little.goose.account.ui.base.BaseActivity
import little.goose.account.utils.KEY_PREF_PAGER
import little.goose.account.utils.homeDataStore
import little.goose.account.utils.viewBinding
import little.goose.account.utils.withData

class MainActivity : BaseActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isAppInit }
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        initViewPager()
        initNavigation()
        recoverPager()
    }

    // 恢复到上次打开的页面
    private fun recoverPager() {
        lifecycleScope.launch {
            homeDataStore.withData(KEY_PREF_PAGER) { pager ->
                binding.homeViewPager.setCurrentItem(pager, false)
                binding.bottomNav.menu.getItem(pager).isChecked = true
            }
        }
    }

    private fun initViewPager() {
        binding.homeViewPager.apply {
            adapter = HomeFragmentPagerAdapter(this@MainActivity)
            isUserInputEnabled = false //禁止左右滑动
        }
    }

    override fun onStop() {
        super.onStop()
        // 保存当前页面
        superScope.launch {
            homeDataStore.edit { home ->
                home[KEY_PREF_PAGER] = binding.homeViewPager.currentItem
            }
        }
    }

    private fun initNavigation() {
        binding.apply {
            bottomNav.itemIconTintList = null //Icon点击不变色
            bottomNav.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_home -> {
                        homeViewPager.setCurrentItem(HOME, false)
                    }
                    R.id.nav_notebook -> {
                        homeViewPager.setCurrentItem(NOTEBOOK, false)
                    }
                    R.id.nav_account -> {
                        homeViewPager.setCurrentItem(ACCOUNT, false)
                    }
                    R.id.nav_schedule -> {
                        homeViewPager.setCurrentItem(SCHEDULE, false)
                    }
                    R.id.nav_memorial -> {
                        homeViewPager.setCurrentItem(MEMORIAL, false)
                    }
//                  R.id.nav_profile -> {
//                      viewPager.setCurrentItem(HomePageConstant.PROFILE, false)
//                  }
                }

                return@setOnItemSelectedListener true
            }
        }
    }

}