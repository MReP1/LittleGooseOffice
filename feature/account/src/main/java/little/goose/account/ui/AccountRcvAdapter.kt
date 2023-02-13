package little.goose.account.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import little.goose.account.data.constants.AccountConstant
import little.goose.common.ItemSelectCallback
import little.goose.common.MultipleChoseHandler
import little.goose.account.databinding.ItemTransactionHolderBinding
import little.goose.account.databinding.ItemTransactionTimeBinding

class AccountRcvAdapter(
    private var itemList: List<little.goose.account.data.entities.Transaction>,
    private val callback: ItemSelectCallback<little.goose.account.data.entities.Transaction>? = null,
    private val multipleChoseHandler: MultipleChoseHandler<little.goose.account.data.entities.Transaction>? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TRANSACTION -> {
                //账单布局
                val binding = ItemTransactionHolderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                TransactionViewHolder(binding, multipleChoseHandler)
            }
            else -> {
                //时间布局
                val binding = ItemTransactionTimeBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                TransactionTimeViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val transaction = itemList[position]
        when (holder) {
            is TransactionViewHolder -> {
                holder.apply {
                    bindData(transaction)
                    itemView.setOnClickListener {
                        if (multipleChoseHandler?.isMultipleChose?.value == true) {
                            multiClick(holder, transaction)
                        } else {
                            callback?.onItemClick(transaction)
                        }
                    }
                    itemView.setOnLongClickListener {
                        if (multipleChoseHandler?.isMultipleChose?.value == false) {
                            multipleChoseHandler.ready()
                            multiClick(holder, transaction)
                        }
                        callback?.onItemLongClick(transaction)
                        return@setOnLongClickListener true
                    }
                }
            }
            is TransactionTimeViewHolder -> {
                holder.bindData(transaction)
            }
        }
    }

    private fun multiClick(holder: TransactionViewHolder, transaction: little.goose.account.data.entities.Transaction) {
        multipleChoseHandler?.let { handler ->
            val flag = handler.clickItem(transaction)
            holder.setSelect(flag)
        }
    }

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int {
        val item = itemList[position]
        return if (item.type == AccountConstant.TIME) {
            ITEM_TIME
        } else {
            ITEM_TRANSACTION
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<little.goose.account.data.entities.Transaction>) {
        itemList = list
        notifyDataSetChanged()
    }

    companion object {
        private const val ITEM_TRANSACTION = 0
        private const val ITEM_TIME = 1
    }
}