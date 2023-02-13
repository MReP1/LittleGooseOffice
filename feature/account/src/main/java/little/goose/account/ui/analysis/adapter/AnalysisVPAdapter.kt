package little.goose.account.ui.analysis.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import little.goose.account.databinding.ItemViewPagerAnalysisBalanceBinding
import little.goose.account.databinding.ItemViewPagerAnalysisBinding
import little.goose.account.ui.analysis.viewholder.BALANCE
import little.goose.account.ui.analysis.viewholder.DEFAULT

class AnalysisVPAdapter(
    private var timeType: Int,
    private var year: Int,
    private var month: Int,
    private var expenseList: List<little.goose.account.data.models.TransactionPercent> = emptyList(),
    private var incomeList: List<little.goose.account.data.models.TransactionPercent> = emptyList(),
    private var expenseTimeList: List<little.goose.account.data.models.TimeMoney> = emptyList(),
    private var incomeTimeList: List<little.goose.account.data.models.TimeMoney> = emptyList(),
    private var balanceList: List<little.goose.account.data.models.TransactionBalance> = emptyList(),
    private var balanceTimeList: List<little.goose.account.data.models.TimeMoney> = emptyList()
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
        expenseList: List<little.goose.account.data.models.TransactionPercent>,
        incomeList: List<little.goose.account.data.models.TransactionPercent>,
        balanceList: List<little.goose.account.data.models.TransactionBalance>,
        expenseTimeList: List<little.goose.account.data.models.TimeMoney>,
        incomeTimeList: List<little.goose.account.data.models.TimeMoney>,
        balanceTimeList: List<little.goose.account.data.models.TimeMoney>
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
            listPercent: List<little.goose.account.data.models.TransactionPercent>,
            listTime: List<little.goose.account.data.models.TimeMoney>,
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
            listPercent: List<little.goose.account.data.models.TransactionPercent>,
            listTime: List<little.goose.account.data.models.TimeMoney>
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
            listPercent: List<little.goose.account.data.models.TransactionPercent>,
            listTime: List<little.goose.account.data.models.TimeMoney>
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

        fun bindData(listBalance: List<little.goose.account.data.models.TransactionBalance>, listTime: List<little.goose.account.data.models.TimeMoney>) {
            if (!isInit) {
                initView(listBalance, listTime)
                isInit = true
            } else {
                updateRecyclerView(listBalance, listTime)
            }
        }

        private fun initView(
            listBalance: List<little.goose.account.data.models.TransactionBalance>,
            listTime: List<little.goose.account.data.models.TimeMoney>
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
            listBalance: List<little.goose.account.data.models.TransactionBalance>,
            listTime: List<little.goose.account.data.models.TimeMoney>
        ) {
            rcvAdapter.updateData(listBalance)
            lineChartRcvAdapter.updateData(listTime)
            titleAdapter.updateView(!listBalance.isNullOrEmpty())
        }
    }
}