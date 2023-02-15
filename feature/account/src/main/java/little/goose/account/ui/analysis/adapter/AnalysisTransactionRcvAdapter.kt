package little.goose.account.ui.analysis.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import little.goose.common.dialog.time.TimeType
import little.goose.account.databinding.ItemTransactionAnalysisBinding
import little.goose.account.ui.TransactionExampleActivity
import little.goose.account.ui.analysis.AnalysisFragmentViewModel.Companion.MONTH
import little.goose.account.ui.analysis.viewholder.AnalysisTransactionRcvHolder
import little.goose.common.utils.setMonth
import little.goose.common.utils.setYear
import java.util.*

class AnalysisTransactionRcvAdapter(
    private var list: List<little.goose.account.data.models.TransactionPercent>,
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
        list: List<little.goose.account.data.models.TransactionPercent>,
        year: Int,
        month: Int
    ) {
        this.year = year
        this.month = month
        this.list = list
        notifyDataSetChanged()
    }
}