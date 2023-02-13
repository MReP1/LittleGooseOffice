package little.goose.account.ui.analysis.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import little.goose.account.databinding.LayoutAnalysisBalanceTitleBinding
import little.goose.account.ui.analysis.viewholder.AnalysisBalanceTitleViewHolder

class AnalysisBalanceTitleRcvAdapter(private var isShow: Boolean = false) :
    RecyclerView.Adapter<AnalysisBalanceTitleViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): AnalysisBalanceTitleViewHolder {
        val binding = LayoutAnalysisBalanceTitleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AnalysisBalanceTitleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnalysisBalanceTitleViewHolder, position: Int) {
        holder.bindView(isShow)
    }

    override fun getItemCount(): Int = 1

    @SuppressLint("NotifyDataSetChanged")
    fun updateView(isShow: Boolean = false) {
        this.isShow = isShow
        notifyDataSetChanged()
    }
}