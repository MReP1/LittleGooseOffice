package little.goose.account.ui

import androidx.recyclerview.widget.RecyclerView
import little.goose.account.databinding.ItemTransactionTimeBinding
import little.goose.account.data.entities.Transaction

class TransactionTimeViewHolder(private val binding: ItemTransactionTimeBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindData(transaction: Transaction) {
        binding.tvTime.text = transaction.description
    }

}