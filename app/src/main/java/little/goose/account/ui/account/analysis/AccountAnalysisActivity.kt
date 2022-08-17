package little.goose.account.ui.account.analysis

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import little.goose.account.databinding.ActivityAccountAnalysisBinding
import little.goose.account.ui.account.analysis.adapter.AnalysisFragmentPagerAdapter
import little.goose.account.ui.base.BaseActivity
import little.goose.account.ui.widget.selector.BottomTimeTypeSelector

class AccountAnalysisActivity : BaseActivity() {

    private lateinit var binding: ActivityAccountAnalysisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initFragmentListener()
    }

    private fun initView() {
        initViewPager()
        initBottomBar()
        initActionBar()
    }

    private fun initActionBar() {
        binding.actionBar.setOnBackClickListener{ finish() }
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