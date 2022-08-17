package little.goose.account.ui.account.analysis.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import little.goose.account.databinding.ItemAnalysisBalanceBinding
import little.goose.account.logic.data.models.TransactionBalance
import little.goose.account.ui.account.analysis.viewholder.AnalysisBalanceRcvViewHolder

class AnalysisBalanceRcvAdapter(
    private var list: List<TransactionBalance>,
    private var timeType: Int
) :
    RecyclerView.Adapter<AnalysisBalanceRcvViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnalysisBalanceRcvViewHolder {
        val binding = ItemAnalysisBalanceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AnalysisBalanceRcvViewHolder(binding, timeType)
    }

    override fun onBindViewHolder(holder: AnalysisBalanceRcvViewHolder, position: Int) {
        val transactionBalance = list[position]
        holder.bindView(transactionBalance)
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<TransactionBalance>) {
        this.list = list
        notifyDataSetChanged()
    }
}