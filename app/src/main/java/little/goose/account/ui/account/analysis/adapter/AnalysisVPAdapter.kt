package little.goose.account.ui.account.analysis.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import little.goose.account.databinding.ItemViewPagerAnalysisBalanceBinding
import little.goose.account.databinding.ItemViewPagerAnalysisBinding
import little.goose.account.logic.data.models.TimeMoney
import little.goose.account.logic.data.models.TransactionBalance
import little.goose.account.logic.data.models.TransactionPercent
import little.goose.account.ui.account.analysis.viewholder.BALANCE
import little.goose.account.ui.account.analysis.viewholder.DEFAULT

class AnalysisVPAdapter(
    private var timeType: Int,
    private var year: Int,
    private var month: Int,
    private var expenseList: List<TransactionPercent> = emptyList(),
    private var incomeList: List<TransactionPercent> = emptyList(),
    private var expenseTimeList: List<TimeMoney> = emptyList(),
    private var incomeTimeList: List<TimeMoney> = emptyList(),
    private var balanceList: List<TransactionBalance> = emptyList(),
    private var balanceTimeList: List<TimeMoney> = emptyList()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType < 2) {
            val binding = ItemViewPagerAnalysisBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            AnalysisVPDefaultViewHolder(binding, timeType, year, month)
        } else {
            val binding = ItemViewPagerAnalysisBalanceBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            AnalysisVPBalanceViewHolder(binding, timeType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            0 -> {
                (holder as? AnalysisVPDefaultViewHolder)?.bindData(
                    expenseList, expenseTimeList, year, month
                )
            }
            1 -> {
                (holder as? AnalysisVPDefaultViewHolder)?.bindData(
                    incomeList, incomeTimeList, year, month
                )
            }
            2 -> {
                (holder as? AnalysisVPBalanceViewHolder)?.bindData(balanceList, balanceTimeList)
            }
        }
    }

    override fun getItemCount(): Int = 3

    override fun getItemViewType(position: Int) = position

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(
        year: Int,
        month: Int,
        expenseList: List<TransactionPercent>,
        incomeList: List<TransactionPercent>,
        balanceList: List<TransactionBalance>,
        expenseTimeList: List<TimeMoney>,
        incomeTimeList: List<TimeMoney>,
        balanceTimeList: List<TimeMoney>
    ) {
        this.year = year
        this.month = month
        this.expenseList = expenseList
        this.incomeList = incomeList
        this.balanceList = balanceList
        this.expenseTimeList = expenseTimeList
        this.incomeTimeList = incomeTimeList
        this.balanceTimeList = balanceTimeList
        notifyDataSetChanged()
    }

    /** —————————————— ViewHolder ——————————————*/
    class AnalysisVPDefaultViewHolder(
        private val binding: ItemViewPagerAnalysisBinding,
        private val timeType: Int,
        private var year: Int,
        private var month: Int
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var transRcvAdapter: AnalysisTransactionRcvAdapter
        private lateinit var circleChartRcvAdapter: AnalysisTransCircleChartRcvAdapter
        private lateinit var lineChartRcvAdapter: AnalysisTransLineChartRcvAdapter

        private var isInit = false

        fun bindData(
            listPercent: List<TransactionPercent>,
            listTime: List<TimeMoney>,
            year: Int,
            month: Int
        ) {
            this.year = year
            this.month = month
            if (!isInit) {
                initView(listPercent, listTime)
                isInit = true
            } else {
                updateRecyclerView(listPercent, listTime)
            }
        }

        private fun initView(
            listPercent: List<TransactionPercent>,
            listTime: List<TimeMoney>
        ) {
            transRcvAdapter = AnalysisTransactionRcvAdapter(listPercent, timeType, year, month)
            circleChartRcvAdapter = AnalysisTransCircleChartRcvAdapter(listPercent)
            lineChartRcvAdapter = AnalysisTransLineChartRcvAdapter(listTime, DEFAULT, timeType)
            val concatAdapter = ConcatAdapter(
                lineChartRcvAdapter,
                circleChartRcvAdapter,
                transRcvAdapter
            )
            binding.rcvTransaction.apply {
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = concatAdapter
            }
        }

        private fun updateRecyclerView(
            listPercent: List<TransactionPercent>,
            listTime: List<TimeMoney>
        ) {
            transRcvAdapter.updateData(listPercent, year, month)
            circleChartRcvAdapter.updateData(listPercent)
            lineChartRcvAdapter.updateData(listTime)
        }
    }

    class AnalysisVPBalanceViewHolder(
        private val binding: ItemViewPagerAnalysisBalanceBinding,
        private val timeType: Int
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var rcvAdapter: AnalysisBalanceRcvAdapter
        private lateinit var lineChartRcvAdapter: AnalysisTransLineChartRcvAdapter
        private lateinit var titleAdapter: AnalysisBalanceTitleRcvAdapter

        private var isInit = false

        fun bindData(listBalance: List<TransactionBalance>, listTime: List<TimeMoney>) {
            if (!isInit) {
                initView(listBalance, listTime)
                isInit = true
            } else {
                updateRecyclerView(listBalance, listTime)
            }
        }

        private fun initView(
            listBalance: List<TransactionBalance>,
            listTime: List<TimeMoney>
        ) {
            binding.apply {
                rcvTransactionBalance.layoutManager = LinearLayoutManager(root.context)
                rcvAdapter = AnalysisBalanceRcvAdapter(listBalance, timeType)
                lineChartRcvAdapter = AnalysisTransLineChartRcvAdapter(listTime, BALANCE, timeType)
                titleAdapter = AnalysisBalanceTitleRcvAdapter(!listBalance.isNullOrEmpty())
                val concatAdapter = ConcatAdapter(lineChartRcvAdapter, titleAdapter, rcvAdapter)
                rcvTransactionBalance.adapter = concatAdapter
            }
        }

        private fun updateRecyclerView(
            listBalance: List<TransactionBalance>,
            listTime: List<TimeMoney>
        ) {
            rcvAdapter.updateData(listBalance)
            lineChartRcvAdapter.updateData(listTime)
            titleAdapter.updateView(!listBalance.isNullOrEmpty())
        }
    }
}