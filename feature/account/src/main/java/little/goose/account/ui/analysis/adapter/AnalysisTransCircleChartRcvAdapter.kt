package little.goose.account.ui.analysis.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import little.goose.account.databinding.LayoutHeaderTransCircleChartBinding
import little.goose.account.ui.analysis.viewholder.AnalysisTransCircleGraphViewHolder

class AnalysisTransCircleChartRcvAdapter(private var list: List<little.goose.account.data.models.TransactionPercent>) :
    RecyclerView.Adapter<AnalysisTransCircleGraphViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): AnalysisTransCircleGraphViewHolder {
        val binding = LayoutHeaderTransCircleChartBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AnalysisTransCircleGraphViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnalysisTransCircleGraphViewHolder, position: Int) {
        holder.bindData(list)
    }

    override fun getItemCount(): Int = 1

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<little.goose.account.data.models.TransactionPercent>) {
        this.list = list
        notifyDataSetChanged()
    }

}