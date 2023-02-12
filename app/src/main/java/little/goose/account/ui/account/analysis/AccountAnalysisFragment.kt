package little.goose.account.ui.account.analysis

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import little.goose.account.R
import little.goose.common.dialog.DateTimePickerBottomDialog
import little.goose.common.dialog.time.TimeType
import little.goose.account.databinding.FragmentAccountAnalysisBinding
import little.goose.account.ui.account.analysis.adapter.AnalysisVPAdapter
import little.goose.account.ui.account.analysis.widget.ItemTabTransactionTypeView
import little.goose.account.ui.widget.selector.MonthSelector
import little.goose.account.ui.widget.selector.MonthSelectorCardView
import little.goose.account.ui.widget.selector.YearSelector
import little.goose.account.ui.widget.selector.YearSelectorCardView
import little.goose.common.utils.launchAndRepeatWithViewLifeCycle
import little.goose.common.utils.viewBinding

class AccountAnalysisFragment : Fragment(R.layout.fragment_account_analysis) {

    private val binding by viewBinding(FragmentAccountAnalysisBinding::bind)

    private lateinit var monthSelector: MonthSelectorCardView
    private lateinit var yearSelector: YearSelectorCardView

    private lateinit var incomeTabView: ItemTabTransactionTypeView
    private lateinit var expenseTabView: ItemTabTransactionTypeView
    private lateinit var balanceTabView: ItemTabTransactionTypeView

    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this)[AnalysisFragmentViewModel::class.java]
    }

    private var connector: TabLayoutMediator? = null

    private var isFirstTimeOpen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initData() {
        viewModel.type = arguments?.getInt(KEY_TYPE, 0) ?: 0
    }

    private fun initView() {
        when (viewModel.type) {
            AnalysisFragmentViewModel.YEAR -> initYear()
            AnalysisFragmentViewModel.MONTH -> initMonth()
        }
        initTabLayout()
        initDataChangeListener()
    }

    private fun initTabLayout() {
        incomeTabView = ItemTabTransactionTypeView(requireContext()).also {
            it.setTitle(R.string.income)
        }
        expenseTabView = ItemTabTransactionTypeView(requireContext()).also {
            it.setTitle(R.string.expense)
        }
        balanceTabView = ItemTabTransactionTypeView(requireContext()).also {
            it.setTitle(R.string.balance)
        }
        binding.apply {
            vpAnalysis.adapter = AnalysisVPAdapter(
                viewModel.type,
                viewModel.yearFlow.value,
                viewModel.monthFlow.value
            )
            tlTransType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    (tab?.customView as? ItemTabTransactionTypeView)?.setSelected()
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    (tab?.customView as? ItemTabTransactionTypeView)?.setUnselected()
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })
            connector = TabLayoutMediator(tlTransType, vpAnalysis) { tab, position ->
                when (position) {
                    0 -> {
                        tab.customView = expenseTabView
                    }
                    1 -> {
                        tab.customView = incomeTabView
                    }
                    2 -> {
                        tab.customView = balanceTabView
                    }
                }
            }.also { it.attach() }
        }
    }

    private fun initYear() {
        binding.vsYearSelector.apply {
            setOnInflateListener { _, inflated ->
                yearSelector = inflated as YearSelectorCardView
            }
            visibility = View.VISIBLE
        }
        yearSelector.setYear(getYear())
        yearSelector.apply {
            setOnThisYearClickListener {
                DateTimePickerBottomDialog.Builder()
                    .setType(TimeType.YEAR)
                    .setTime(yearSelector.getYear())
                    .setConfirmAction { yearSelector.setYear(it) }
                    .showNow(parentFragmentManager)
            }
            setOnYearSelectListener(object : YearSelector.OnYearSelectListener {
                override fun onYearSelect(year: Int) {
                    if (year != getYear()) {
                        viewModel.setYear(year)
                    }
                }
            })
        }
        initYearChangeListener()
    }

    private fun initMonth() {
        binding.vsMonthSelector.apply {
            setOnInflateListener { _, inflated ->
                monthSelector = inflated as MonthSelectorCardView
            }
            visibility = View.VISIBLE
        }
        monthSelector.setTime(getYear(), getMonth())
        monthSelector.apply {
            setOnThisMonthClickListener {
                val timeMap = getTime()
                val year = timeMap[MonthSelector.YEAR] ?: 2001
                val month = timeMap[MonthSelector.MONTH] ?: 1
                DateTimePickerBottomDialog.Builder()
                    .setType(TimeType.YEAR_MONTH)
                    .setTime(year, month)
                    .setConfirmAction { monthSelector.setTime(it) }
                    .showNow(parentFragmentManager)
            }
            setOnMonthSelectListener(object : MonthSelector.OnMonthSelectListener {
                override fun onMonthSelect(year: Int, month: Int) {
                    if (getMonth() == month && getYear() != year) {
                        //如果选择只改变年份
                        onMonthChange(year, month)
                    }
                    viewModel.setTime(year, month)
                }
            })
        }
        initMonthChangeListener()
    }

    /**
     * ———————————这里以下监听数据变化—————————————
     * */
    private fun initYearChangeListener() {
        launchAndRepeatWithViewLifeCycle {
            viewModel.yearFlow.collect { year ->
                onYearChange(year)
            }
        }
    }

    private fun onYearChange(year: Int) {
        setTimeResult(year, -1)
        viewModel.updateTransactionListYear()
    }

    private fun initMonthChangeListener() {
        launchAndRepeatWithViewLifeCycle {
            viewModel.monthFlow.collect { month ->
                onMonthChange(getYear(), month)
            }
        }
    }

    private fun onMonthChange(year: Int = getYear(), month: Int = getMonth()) {
        setTimeResult(year, month)
        viewModel.updateTransactionListMonth()
    }

    /**
     * ———————————监听列表变化—————————————
     * */
    private fun initDataChangeListener() {
        launchAndRepeatWithViewLifeCycle {
            viewModel.transactionList.collect {
                //刷新ViewPager
                (binding.vpAnalysis.adapter as AnalysisVPAdapter).updateData(
                    viewModel.yearFlow.value,
                    viewModel.monthFlow.value,
                    viewModel.getListExpensePercent(),
                    viewModel.getListIncomePercent(),
                    viewModel.getListTransactionBalance(),
                    viewModel.getTimeExpenseList(),
                    viewModel.getTimeIncomeList(),
                    viewModel.getTimeBalanceList()
                )
                //刷新TabLayout
                expenseTabView.setMoney(viewModel.getExpenseSumStr())
                incomeTabView.setMoney(viewModel.getIncomeSumStr())
                balanceTabView.setMoney(viewModel.getBalanceStr())
            }
        }
    }

    //让外界Activity的界面变化
    private fun setTimeResult(year: Int, month: Int) {
        val bundle = Bundle().apply {
            putInt(KEY_YEAR, year)
            putInt(KEY_MONTH, month)
        }
        parentFragmentManager.setFragmentResult(KEY_TIME_CHANGE_RESULT, bundle)
    }

    override fun onResume() {
        super.onResume()
        if (isFirstTimeOpen) {
            isFirstTimeOpen = false
        } else {
            resumeChangeOutside()
            resumeResetTabLayout()
        }
    }

    //切换年月视图的时候需要手动告知一次外界Activity界面变化
    private fun resumeChangeOutside() {
        if (isFirstTimeOpen) {
            isFirstTimeOpen = false
        } else {
            when (viewModel.type) {
                AnalysisFragmentViewModel.YEAR -> setTimeResult(getYear(), -1)
                AnalysisFragmentViewModel.MONTH -> setTimeResult(getYear(), getMonth())
            }
        }
    }

    //切换年月视图的时候需要重新设置，不然有水波纹消失的BUG
    private fun resumeResetTabLayout() {
        binding.apply {
            connector?.detach()
            connector?.attach()
            incomeTabView.updateTabView(0)
            expenseTabView.updateTabView(1)
            balanceTabView.updateTabView(2)
        }
    }

    //解决奇怪的BUG
    private fun ItemTabTransactionTypeView.updateTabView(position: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val selectPosition = binding.tlTransType.selectedTabPosition
            delay(50)
            if (binding.tlTransType.selectedTabPosition != selectPosition) {
                if (position == selectPosition) {
                    this@updateTabView.setSelected()
                } else {
                    this@updateTabView.setUnselected()
                }
            }
        }
    }

    private fun getYear() = viewModel.yearFlow.value
    private fun getMonth() = viewModel.monthFlow.value

    companion object {
        const val KEY_TIME_CHANGE_RESULT = "time_change_result"
        const val KEY_YEAR = "year"
        const val KEY_MONTH = "month"
        private const val KEY_TYPE = "type"
        fun newInstance(type: Int): AccountAnalysisFragment {
            val bundle = Bundle().also { it.putInt(KEY_TYPE, type) }
            return AccountAnalysisFragment().apply {
                arguments = bundle
            }
        }

    }
}