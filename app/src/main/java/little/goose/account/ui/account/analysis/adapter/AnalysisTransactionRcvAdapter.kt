package little.goose.account.ui.account.analysis.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import little.goose.account.common.dialog.time.TimeType
import little.goose.account.databinding.ItemTransactionAnalysisBinding
import little.goose.account.logic.data.models.TransactionPercent
import little.goose.account.ui.account.TransactionExampleActivity
import little.goose.account.ui.account.analysis.AnalysisFragmentViewModel.Companion.MONTH
import little.goose.account.ui.account.analysis.viewholder.AnalysisTransactionRcvHolder
import little.goose.account.utils.setMonth
import little.goose.account.utils.setYear
import java.util.*

class AnalysisTransactionRcvAdapter(
    private var list: List<TransactionPercent>,
    private var timeType: Int,
    private var year: Int,
    private var month: Int
) : RecyclerView.Adapter<AnalysisTransactionRcvHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): AnalysisTransactionRcvHolder {
        val binding = ItemTransactionAnalysisBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AnalysisTransactionRcvHolder(binding)
    }

    override fun onBindViewHolder(holder: AnalysisTransactionRcvHolder, position: Int) {
        holder.bindData(list[position])
        holder.itemView.setOnClickListener {
            val calendar = Calendar.getInstance().apply {
                clear()
                setYear(year)
                setMonth(month)
            }
            TransactionExampleActivity.open(
                holder.itemView.context,
                calendar.time,
                if (timeType == MONTH) {
                    TimeType.YEAR_MONTH
                } else TimeType.YEAR,
                keyContent = list[position].content
            )
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(
        list: List<TransactionPercent>,
        year: Int,
        month: Int
    ) {
        this.year = year
        this.month = month
        this.list = list
        notifyDataSetChanged()
    }
}