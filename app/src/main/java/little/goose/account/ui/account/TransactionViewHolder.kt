package little.goose.account.ui.account

import androidx.recyclerview.widget.RecyclerView
import little.goose.account.common.MultipleChoseHandler
import little.goose.account.databinding.ItemTransactionHolderBinding
import little.goose.account.logic.data.entities.Transaction

class TransactionViewHolder(
    private val binding: ItemTransactionHolderBinding,
    private val multipleChoseHandler: MultipleChoseHandler<Transaction>?
) : RecyclerView.ViewHolder(binding.root) {

    fun bindData(transaction: Transaction) {
        binding.apply {
            itemTransaction.bindData(transaction)
        }
        //fixme 需要优化
        multipleChoseHandler?.itemList?.contains(transaction)?.let { setSelect(it) }
    }

    fun setSelect(flag: Boolean) {
        binding.itemTransaction.setSelect(flag)
    }
}