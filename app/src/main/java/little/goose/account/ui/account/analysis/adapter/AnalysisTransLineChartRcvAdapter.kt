package little.goose.account.ui.account.analysis.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import little.goose.account.databinding.LayoutHeaderTransLineChartBinding
import little.goose.account.logic.data.models.TimeMoney
import little.goose.account.ui.account.analysis.viewholder.AnalysisTransLineChartRcvViewHolder
import little.goose.account.ui.account.analysis.viewholder.DEFAULT

class AnalysisTransLineChartRcvAdapter(
    private var list: List<TimeMoney>,
    private val type: Int = DEFAULT,
    private val timeType: Int
) : RecyclerView.Adapter<AnalysisTransLineChartRcvViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnalysisTransLineChartRcvViewHolder {
        val binding = LayoutHeaderTransLineChartBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AnalysisTransLineChartRcvViewHolder(binding, timeType)
    }

    override fun onBindViewHolder(holder: AnalysisTransLineChartRcvViewHolder, position: Int) {
        holder.bindData(list, type)
    }

    override fun getItemCount(): Int = 1

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<TimeMoney>){
        this.list = list
        notifyDataSetChanged()
    }

}