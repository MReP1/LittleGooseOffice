package little.goose.account.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import little.goose.account.R
import little.goose.account.data.entities.Transaction
import little.goose.account.databinding.FragmentAccountBinding
import little.goose.account.ui.analysis.AccountAnalysisActivity
import little.goose.account.ui.component.AccountTitle
import little.goose.account.ui.component.AccountTitleState
import little.goose.account.ui.component.MonthSelectorState
import little.goose.account.ui.component.TransactionColumn
import little.goose.account.ui.transaction.TransactionActivity
import little.goose.account.ui.transaction.TransactionDialogFragment
import little.goose.account.ui.transaction.insertTime
import little.goose.account.ui.widget.MonthSelector
import little.goose.account.utils.*
import little.goose.common.ItemSelectCallback
import little.goose.common.MultipleChoseHandler
import little.goose.common.constants.NOTIFY_DELETE_TRANSACTION
import little.goose.common.decoration.ItemLinearLayoutDecoration
import little.goose.common.dialog.DateTimePickerCenterDialog
import little.goose.common.dialog.NormalDialogFragment
import little.goose.common.dialog.time.TimeType
import little.goose.common.utils.*
import kotlin.properties.Delegates

@SuppressLint("NotifyDataSetChanged")
@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.fragment_account) {

    private val viewModel: AccountFragmentViewModel by viewModels()

    private val binding by viewBinding(FragmentAccountBinding::bind)

    private var itemSelectCallback: ItemSelectCallback<Transaction>? = null

    private val multipleChoseHandler = MultipleChoseHandler<Transaction>()

    private val addOnClickListener = View.OnClickListener {
        TransactionActivity.openAdd(requireContext())
    }

    private var isCurMonth by Delegates.observable(true) { _, _, newValue ->
        updateHeader(newValue)
    }

    private val deleteOnClickListener = View.OnClickListener {
        NormalDialogFragment.Builder()
            .setContent(getString(little.goose.common.R.string.confirm_delete))
            .setConfirmCallback {
                viewLifecycleOwner.lifecycleScope.launch {
                    multipleChoseHandler.deleteItemList {
                        viewModel.deleteTransactions(it)
                        binding.root.showSnackbar(
                            little.goose.common.R.string.deleted,
                            1000,
                            little.goose.common.R.string.undo
                        ) {
                            viewModel.addTransactions(it)
                        }
                    }
                }
            }
            .showNow(parentFragmentManager)
    }

    private var updateJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.deleteReceiver.register(
            requireContext(),
            lifecycle,
            NOTIFY_DELETE_TRANSACTION
        ) { _, transaction ->
            binding.root.showSnackbar(
                little.goose.common.R.string.deleted,
                1000,
                little.goose.common.R.string.undo
            ) {
                viewModel.addTransaction(transaction)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCallback()  //设置RecyclerView点击CallBack
        initTransactionRecyclerView()
        initFlowListener()
        updateHeader(isCurMonth)
        initButton()
        initMultipleChose()
        initMonthSelector()
    }

    private fun initButton() {
        binding.apply {
            cvAccountHeader.setOnClickListener {
                isCurMonth = !isCurMonth
            }
            multiButton.apply {
                setOnFloatButtonClickListener(addOnClickListener)
                setOnFloatVectorClickListener { cancelMultiChose() }
                setOnBackPressListener { cancelMultiChose() }
                setButtonAllVisibility(false)
                setOnFloatUpClickListener { AccountAnalysisActivity.open(requireContext()) }
                setOnFloatSideClickListener {
//                    SearchActivity.open(requireContext(), ACCOUNT)
                }
            }
        }
    }

    private fun initTransactionRecyclerView() {
        binding.rcvAccount.apply {
            addItemDecoration(ItemLinearLayoutDecoration(dp16, dp16, 10.dp()))
            layoutManager = LinearLayoutManager(this@AccountFragment.requireContext())
            viewLifecycleOwner.lifecycleScope.launch {
                val listIncludeTime = viewModel.curMonthTransactionWithTime.value
                adapter =
                    AccountRcvAdapter(listIncludeTime, itemSelectCallback, multipleChoseHandler)
            }
        }
    }

    private fun initFlowListener() {
        updateJob?.cancel()
        updateJob = viewModel.curMonthTransactionFlow.collectLastWithLifecycle(lifecycle) {
            val listIncludeTime = it.insertTime()
            updateHeader(isCurMonth)
            (binding.rcvAccount.adapter as? AccountRcvAdapter)?.updateData(listIncludeTime)
        }
    }

    private fun initMultipleChose() {
        launchAndRepeatWithViewLifeCycle {
            multipleChoseHandler.isMultipleChose.collect { isMulti ->
                binding.apply {
                    if (isMulti) {
                        multiButton.showDelete()
                        multiButton.setOnFloatButtonClickListener(deleteOnClickListener)
                    } else {
                        multiButton.hideDelete()
                        multiButton.setOnFloatButtonClickListener(addOnClickListener)
                    }
                }
            }
        }
    }

    private fun initMonthSelector() {
        binding.llMonthSelect.apply {
            setOnThisMonthClickListener {
                val timeMap = getTime()
                val year = timeMap[MonthSelector.YEAR] ?: 2001
                val month = timeMap[MonthSelector.MONTH] ?: 1
                DateTimePickerCenterDialog.Builder()
                    .setConfirmAction {
                        setTime(it)
                    }
                    .setTime(year, month)
                    .setCenterWindow(
                        UIUtils.getWidthPercentPixel(0.6F),
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    .setType(TimeType.YEAR_MONTH)
                    .showNow(parentFragmentManager)
            }
            setOnMonthSelectListener(object :
                MonthSelector.OnMonthSelectListener {
                override fun onMonthSelect(year: Int, month: Int) {
                    updateTransactionRecyclerView(year, month)
                }
            })
        }
    }

    private fun updateTransactionRecyclerView(year: Int, month: Int) {
        updateJob?.cancel()
        updateJob = launchAndRepeatWithViewLifeCycle {
            viewModel.getTransactionByYearAndMonthFlow(year, month).collect {
                val listIncludeTime = async(Dispatchers.Default) { it.insertTime() }
                updateHeader(isCurMonth)
                (binding.rcvAccount.adapter as? AccountRcvAdapter)?.updateData(listIncludeTime.await())
            }
        }
    }

    private fun updateHeader(isCurMonth: Boolean) {
        binding.apply {
            if (isCurMonth) {
                tvTitleExpense.text = getString(R.string.current_month_expense)
                tvTitleIncome.text = getString(R.string.current_month_income)
                tvTitleBalance.text = getString(R.string.current_month_balance)
                tvExpense.text = viewModel.curMonthExpenseSum.value.toPlainString()
                tvIncome.text = viewModel.curMonthIncomeSum.value.toPlainString()
                tvBalance.text = viewModel.curMonthBalance.value.toPlainString()
            } else {
                tvTitleExpense.text = getString(R.string.expense)
                tvTitleIncome.text = getString(R.string.income)
                tvTitleBalance.text = getString(R.string.balance)
                tvExpense.text = viewModel.totalExpenseSum.value.toPlainString()
                tvIncome.text = viewModel.totalIncomeSum.value.toPlainString()
                tvBalance.text = viewModel.totalBalance.value.toPlainString()
            }
        }
    }

    private fun setCallback() {
        itemSelectCallback = object : ItemSelectCallback<Transaction> {
            override fun onItemClick(item: Transaction) {
                TransactionDialogFragment.showNow(item, parentFragmentManager)
            }

            override fun onItemLongClick(item: Transaction) {}
        }
    }

    private fun cancelMultiChose() {
        multipleChoseHandler.release()
        binding.rcvAccount.adapter?.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        itemSelectCallback = null
    }

    override fun onResume() {
        super.onResume()
        if (multipleChoseHandler.needRefresh) {
            binding.rcvAccount.adapter?.notifyDataSetChanged()
        }
    }

    override fun onPause() {
        super.onPause()
        multipleChoseHandler.setNeedRefresh()
    }

    companion object {
        fun newInstance(): AccountFragment = AccountFragment()
    }
}

@Composable
fun AccountRoute(
    modifier: Modifier = Modifier,
    onTransactionClick: (Transaction) -> Unit
) {
    val viewModel = viewModel<AccountFragmentViewModel>()
    val transactions by viewModel.curMonthTransactionWithTime.collectAsState()
    val accountTitleState by viewModel.accountTitleState.collectAsState()
    val monthSelectorState by viewModel.monthSelectorState.collectAsState()
    AccountScreen(
        modifier = modifier,
        transactionsWithTime = transactions,
        accountTitleState = accountTitleState,
        onTransactionClick = onTransactionClick,
        monthSelectorState = monthSelectorState
    )
}

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    transactionsWithTime: List<Transaction>,
    onTransactionClick: (Transaction) -> Unit,
    accountTitleState: AccountTitleState,
    monthSelectorState: MonthSelectorState
) {
    Column(modifier) {
        AccountTitle(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            accountTitleState = accountTitleState,
            monthSelectorState = monthSelectorState
        )
        TransactionColumn(
            modifier = Modifier.weight(1F),
            transactions = transactionsWithTime,
            onTransactionClick = onTransactionClick
        )
    }
}