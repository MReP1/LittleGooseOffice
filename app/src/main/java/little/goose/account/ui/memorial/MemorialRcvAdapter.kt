package little.goose.account.ui.memorial

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import little.goose.account.common.ItemClickCallback
import little.goose.account.common.ItemSelectCallback
import little.goose.account.common.MultipleChoseHandler
import little.goose.account.databinding.ItemMemorialBinding
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.logic.data.entities.Transaction

class MemorialRcvAdapter(
    private var list: List<Memorial> = emptyList(),
    private var multipleChoseHandler: MultipleChoseHandler<Memorial>? = null,
    private val callback: ItemClickCallback<Memorial>? = null
) : RecyclerView.Adapter<MemorialRcvViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemorialRcvViewHolder {
        val binding = ItemMemorialBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MemorialRcvViewHolder(binding, multipleChoseHandler)
    }

    override fun onBindViewHolder(holder: MemorialRcvViewHolder, position: Int) {
        val memorial = list[position]
        holder.bindView(memorial)
        holder.itemView.setOnClickListener {
            if (multipleChoseHandler?.isMultipleChose?.value == true) {
                multiClick(holder, memorial)
            } else {
                callback?.onItemClick(memorial)
            }
        }
        holder.itemView.setOnLongClickListener {
            if (multipleChoseHandler?.isMultipleChose?.value == false) {
                multipleChoseHandler?.ready()
                multiClick(holder, memorial)
            }
            return@setOnLongClickListener true
        }
    }

    private fun multiClick(holder: MemorialRcvViewHolder, memorial: Memorial) {
        multipleChoseHandler?.let { handler ->
            val isSelect = handler.clickItem(memorial)
            holder.setSelect(isSelect)
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<Memorial>) {
        this.list = list
        notifyDataSetChanged()
    }
}