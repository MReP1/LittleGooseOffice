package little.goose.account.ui.home

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import little.goose.account.R
import little.goose.account.appScope
import little.goose.common.dialog.DateTimePickerCenterDialog
import little.goose.account.databinding.FragmentHomeBinding
import little.goose.account.logic.AccountRepository
import little.goose.memorial.logic.MemorialRepository
import little.goose.account.logic.ScheduleRepository
import little.goose.account.logic.data.constant.*
import little.goose.account.logic.data.constant.AccountConstant.EXPENSE
import little.goose.account.logic.data.constant.AccountConstant.INCOME
import little.goose.memorial.data.entities.Memorial
import little.goose.account.logic.data.entities.Schedule
import little.goose.account.logic.data.entities.Transaction
import little.goose.account.ui.account.transaction.TransactionActivity
import little.goose.account.ui.account.transaction.TransactionHelper
import little.goose.account.ui.base.BaseFragment
import little.goose.memorial.ui.MemorialDialogFragment
import little.goose.account.ui.schedule.ScheduleDialogFragment
import little.goose.account.utils.*
import little.goose.common.constants.NOTIFY_DELETE_MEMORIAL
import little.goose.common.constants.NOTIFY_DELETE_SCHEDULE
import little.goose.common.constants.NOTIFY_DELETE_TRANSACTION
import little.goose.memorial.utils.appendTimeSuffix
import little.goose.memorial.utils.getMapDayBoolean
import kotlin.properties.Delegates

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()
    private val binding by viewBinding(FragmentHomeBinding::bind)

    private val redColor by lazy { ContextCompat.getColor(requireContext(), R.color.add_button) }

    private var year = 0
    private var month = 0
    private var day = 0
    private var week = "null"

    private var isAccountUnfold: Boolean by Delegates.observable(false) { _, _, newValue ->
        binding.rcvTransaction.updateAdapter(listTransaction, newValue)
        binding.ivAccountUnfold.updateArrow(newValue)
    }
    private var isScheduleUnfold: Boolean by Delegates.observable(false) { _, _, newValue ->
        binding.rcvSchedule.updateAdapter(listSchedule, newValue)
        binding.ivScheduleUnfold.updateArrow(newValue)
    }
    private var listTransaction: List<Transaction> by Delegates.observable(emptyList()) { _, _, newValue ->
        binding.rcvTransaction.updateAdapter(newValue, isAccountUnfold)
    }
    private var listSchedule: List<Schedule> by Delegates.observable(emptyList()) { _, _, newValue ->
        binding.rcvSchedule.updateAdapter(newValue, isScheduleUnfold)
    }
    private var listMemorial: List<Memorial> by Delegates.observable(emptyList()) { _, _, newValue ->
        val memorial = newValue.firstOrNull() ?: return@observable
        updateMemorialCard(memorial)
    }

    private var isFirstInit = 0

    private fun RecyclerView.updateAdapter(list: List<Any>, isUnfold: Boolean) {
        //只有两个RecyclerView需要初始化
        if (isFirstInit < 2) {
            this.initAdapter(list, isUnfold)
            isFirstInit++
        } else {
            if (isUnfold) {
                (this.adapter as? HomeWidgetRcvAdapter)?.updateData(list.take(10))
            } else {
                (this.adapter as? HomeWidgetRcvAdapter)?.updateData(list.take(3))
            }
        }
    }

    private fun RecyclerView.initAdapter(list: List<Any>, isUnfold: Boolean) {
        if (isUnfold) {
            this.adapter = HomeWidgetRcvAdapter(list.take(10), parentFragmentManager)
        } else {
            this.adapter = HomeWidgetRcvAdapter(list.take(3), parentFragmentManager)
        }
    }

    private fun ImageView.updateArrow(isUnfold: Boolean) {
        if (isUnfold) {
            this.setImageResource(R.drawable.icon_arrow_up)
        } else {
            this.setImageResource(R.drawable.icon_arrow_drop)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.scheduleDeleteReceiver.register(
            requireContext(),
            lifecycle,
            NOTIFY_DELETE_SCHEDULE
        ) { _, schedule ->
            binding.root.showDeleteSnackbar {
                appScope.launch {
                    ScheduleRepository.addSchedule(schedule)
                }
            }
        }
        viewModel.transactionDeleteReceiver.register(
            requireContext(),
            lifecycle,
            NOTIFY_DELETE_TRANSACTION
        ) { _, transaction ->
            binding.root.showDeleteSnackbar {
                appScope.launch {
                    AccountRepository.addTransaction(transaction)
                }
            }
        }
        viewModel.memorialDeleteReceiver.register(
            requireContext(),
            lifecycle,
            NOTIFY_DELETE_MEMORIAL
        ) { _, memorial ->
            binding.root.showDeleteSnackbar {
                viewModel.addMemorial(memorial)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
    }

    private fun initView() {
        initTitleBar()
        initCalendar()
        initFlowListener()
        initClick()
        initTransactionRecyclerView()
        initScheduleRecyclerView()
    }

    private fun initCalendar() {
        binding.apply {
            //初始化刚打开第一个月的scheme
            updateScheme(year, month)

            calendarView.setOnCalendarSelectListener(object :
                CalendarView.OnCalendarSelectListener {
                override fun onCalendarOutOfRange(calendar: Calendar?) {}
                override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
                    if (calendar == null)
                        return
                    if (year == calendar.year && month == calendar.month && day == calendar.day)
                        return
                    year = calendar.year
                    month = calendar.month
                    day = calendar.day
                    week = DateTimeUtils.getWeekDayZ(calendar.week + 1)
                    updateTransactionList()
                    updateScheduleList()
                    updateMemorialList()
                    updateTitleBar()
                }
            })

            calendarView.setOnMonthChangeListener { year, month ->
                updateScheme(year, month)
            }

            tvMonthDate.setOnClickListener {
                DateTimePickerCenterDialog.Builder()
                    .setTime(year, month, day)
                    .setConfirmAction {
                        java.util.Calendar.getInstance().apply {
                            time = it
                            calendarView.scrollToCalendar(getYear(), getMonth(), getDate(), true)
                        }
                    }
                    .showNow(parentFragmentManager)
            }
        }
    }

    private fun initData() {
        binding.calendarView.apply {
            year = curYear
            month = curMonth
            day = curDay
            week = DateTimeUtils.getWeekFormYearMonthDateZ(year, month, day)
        }
    }

    private fun initTransactionRecyclerView() {
        binding.rcvTransaction.apply {
            layoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            updateAdapter(listTransaction, isAccountUnfold)
        }
        binding.ivAccountUnfold.setOnClickListener {
            isAccountUnfold = !isAccountUnfold
        }
    }

    private fun initScheduleRecyclerView() {
        binding.rcvSchedule.apply {
            layoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            updateAdapter(listSchedule, isScheduleUnfold)
        }
        binding.ivScheduleUnfold.setOnClickListener {
            isScheduleUnfold = !isScheduleUnfold
        }
    }

    private fun initClick() {
        binding.apply {
            tvAddTransaction.setOnClickListener {
                TransactionActivity.openAdd(
                    this@HomeFragment.requireContext(),
                    DateTimeUtils.getSelectDateOfThisTime(year, month, day)
                )
            }
            tvAddSchedule.setOnClickListener {
                ScheduleDialogFragment.newInstance(
                    time = DateTimeUtils.getSelectDateOfThisTime(year, month, day)
                ).showNow(parentFragmentManager, "schedule")
            }
        }
    }

    private fun updateTransactionList() {
        viewModel.updateOneDayTransactionListFlow(year, month, day)
        initTransactionJob()
    }

    private fun updateScheduleList() {
        viewModel.updateOneDayScheduleListFlow(year, month, day)
        initScheduleJob()
    }

    private fun updateMemorialList() {
        viewModel.updateOneDayMemorialListFLow(year, month, day)
        initMemorialJob()
    }

    private fun updateMemorialCard(memorial: Memorial) {
        binding.apply {
            tvMemoContent.setBackgroundColor(
                if (memorial.time.isFuture()) {
                    ContextCompat.getColor(requireContext(), R.color.red_500)
                } else {
                    ContextCompat.getColor(requireContext(), R.color.green_500)
                }
            )
            tvMemoContent.text = memorial.content.appendTimeSuffix(memorial.time, requireContext())
            tvMemoTime.setTime(memorial.time)
            cardMemorial.setOnClickListener {
                MemorialDialogFragment.newInstance(memorial)
                    .showNow(parentFragmentManager, KEY_MEMORIAL)
            }
        }
    }

    private fun initTitleBar() {
        binding.apply {
            updateTitleBar()
            tvCurrentDate.text = DateTimeUtils.getCurrentDate().toString()
            clCalendar.setOnClickListener {
                calendarView.scrollToCurrent(true)
            }
        }
    }

    private fun updateTitleBar() {
        binding.apply {
            tvMonthDate.text = getString(R.string.some_month_date, month.toString(), day.toString())
            tvYear.text = year.toString()
            tvWeekDay.text = week
        }
    }

    private fun initFlowListener() {
        initTransactionJob()
        initScheduleJob()
        initMemorialJob()
    }

    private var scheduleJob: Job? = null
    private var transactionJob: Job? = null
    private var memorialJob: Job? = null

    private fun initTransactionJob() {
        transactionJob?.cancel()
        transactionJob = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.transactionListFlow.collect {
                listTransaction = it
                binding.apply {
                    if (listTransaction.isEmpty()) {
                        setTransactionCardVisibility(false)
                    } else {
                        setTransactionCardVisibility(true)
                        TransactionHelper.getSumFromTransactionList(it).apply {
                            tvExpenseMoney.text = get(EXPENSE)?.toPlainString() ?: "0"
                            tvIncomeMoney.text = get(INCOME)?.toPlainString() ?: "0"
                        }
                    }
                }
                updateScheme(year, month)
            }
        }
    }

    private fun initScheduleJob() {
        scheduleJob?.cancel()
        scheduleJob = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.scheduleListFlow.collect {
                listSchedule = it
                if (listSchedule.isEmpty()) {
                    setScheduleCardVisibility(false)
                } else {
                    setScheduleCardVisibility(true)
                }
                updateScheme(year, month)
            }
        }
    }

    private fun initMemorialJob() {
        memorialJob?.cancel()
        memorialJob = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.memorialListFlow.collect {
                listMemorial = it
                if (listMemorial.isEmpty()) {
                    setMemorialCardVisibility(false)
                } else {
                    setMemorialCardVisibility(true)
                }
                updateScheme(year, month)
            }
        }
    }

    private fun setTransactionCardVisibility(visible: Boolean) {
        binding.apply {
            if (visible) {
                tvNoTransaction.setGone()
                tvIncome.setVisible()
                tvIncomeMoney.setVisible()
                tvExpense.setVisible()
                tvExpenseMoney.setVisible()
                rcvTransaction.setVisible()
                ivAccountUnfold.setVisible()
            } else {
                tvNoTransaction.setVisible()
                tvIncome.setGone()
                tvIncomeMoney.setGone()
                tvExpense.setGone()
                tvExpenseMoney.setGone()
                rcvTransaction.setGone()
                ivAccountUnfold.setGone()
            }
        }
    }

    private fun setScheduleCardVisibility(visible: Boolean) {
        binding.apply {
            if (visible) {
                rcvSchedule.setVisible()
                ivScheduleUnfold.setVisible()
                tvNoSchedule.setGone()
            } else {
                rcvSchedule.setGone()
                ivScheduleUnfold.setGone()
                tvNoSchedule.setVisible()
            }
        }
    }

    private fun setMemorialCardVisibility(visible: Boolean) {
        binding.apply {
            if (visible) {
                cardMemorial.setVisible()
            } else {
                cardMemorial.setGone()
            }
        }
    }

    companion object {
        fun newInstance() = HomeFragment()
    }

    @Volatile
    private var lastUpdateTime = 0L

    private fun updateScheme(year: Int, month: Int) {
        //有很多地方需要更新，防止重复更新
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastUpdateTime < 60) return
        lastUpdateTime = currentTime
        lifecycleScope.launch {
            binding.calendarView.setSchemeDate(getMapOfCalendarScheme(year, month))
        }
    }

    private suspend fun getMapOfCalendarScheme(year: Int, month: Int) =
        withContext((Dispatchers.Default)) {

            //异步获取
            val mapDayMoneyDeferred = async(Dispatchers.IO) {
                AccountRepository.getTransactionsByYearAndMonth(year, month).getMapDayMoney()
            }

            val mapSchedulesDeferred = async(Dispatchers.IO) {
                ScheduleRepository.getScheduleByYearMonth(year, month).getMapDayBoolean()
            }

            val mapMemorialsDeferred = async(Dispatchers.IO) {
                viewModel.getMemorialsByYearMonth(year, month).getMapDayBoolean()
            }

            val mapDayMoney = mapDayMoneyDeferred.await()
            val mapSchedule = mapSchedulesDeferred.await()
            val mapMemorial = mapMemorialsDeferred.await()

            val map = HashMap<String, Calendar>()

            for (day in 1..31) {
                var calendar: Calendar? = null
                if (mapDayMoney.containsKey(day)) {
                    val money = mapDayMoney[day]!!
                    if (calendar == null) {
                        calendar = CalendarViewUtils.getSchemeCalendar(year, month, day)
                    }
                    calendar.addScheme(Calendar.Scheme(ACCOUNT, redColor, money.toSignString()))
                }
                if (mapSchedule.containsKey(day)) {
                    if (calendar == null) {
                        calendar = CalendarViewUtils.getSchemeCalendar(year, month, day)
                    }
                    calendar.addScheme(Calendar.Scheme(SCHEDULE, redColor, null))
                }
                if (mapMemorial.containsKey(day)) {
                    if (calendar == null) {
                        calendar = CalendarViewUtils.getSchemeCalendar(year, month, day)
                    }
                    calendar.addScheme(Calendar.Scheme(MEMORIAL, redColor, null))
                }
                calendar?.let { map[it.toString()] = it }
            }

            map
        }

}