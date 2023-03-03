package little.goose.account.ui.analysis

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import little.goose.account.databinding.ActivityAccountAnalysisBinding
import little.goose.account.ui.analysis.adapter.AnalysisFragmentPagerAdapter
import little.goose.account.ui.widget.BottomTimeTypeSelector
import little.goose.common.utils.viewBinding
import little.goose.design.system.theme.AccountTheme

@AndroidEntryPoint
class AccountAnalysisActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityAccountAnalysisBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccountTheme {
                TransactionAnalysisScreen(modifier = Modifier.fillMaxSize())
            }
        }
//        setContentView(binding.root)
//        initView()
//        initFragmentListener()
    }

    private fun initView() {
        initViewPager()
        initBottomBar()
    }

    private fun initViewPager() {
        binding.vpAnalysis.apply {
            adapter = AnalysisFragmentPagerAdapter(this@AccountAnalysisActivity)
            isUserInputEnabled = false
            setCurrentItem(1, false)
        }
    }

    private fun initBottomBar() {
        binding.timeTypeSelector.apply {
            setOnTypeChangeListener(object : BottomTimeTypeSelector.OnTypeChangeListener {
                override fun onYearSelect(view: TextView) {
                    binding.vpAnalysis.setCurrentItem(0, false)
                }

                override fun onMonthSelect(view: TextView) {
                    binding.vpAnalysis.setCurrentItem(1, false)
                }
            })
        }
    }

    private fun initFragmentListener() {
        supportFragmentManager.setFragmentResultListener(
            AccountAnalysisFragment.KEY_TIME_CHANGE_RESULT,
            this
        ) { resultKey, result ->
            if (resultKey == AccountAnalysisFragment.KEY_TIME_CHANGE_RESULT) {
                val year = result.getInt(AccountAnalysisFragment.KEY_YEAR)
                val month = result.getInt(AccountAnalysisFragment.KEY_MONTH)
                binding.timeTypeSelector.setYear(year)
                binding.timeTypeSelector.setMonth(month)
            }
        }
    }

    companion object {
        fun open(context: Context) {
            val intent = Intent(context, AccountAnalysisActivity::class.java)
            context.startActivity(intent)
        }
    }
}