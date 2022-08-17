package little.goose.account.ui.account.analysis.viewholder

import androidx.recyclerview.widget.RecyclerView
import little.goose.account.common.dialog.time.TimeType
import little.goose.account.databinding.ItemAnalysisBalanceBinding
import little.goose.account.logic.data.models.TransactionBalance
import little.goose.account.ui.account.TransactionExampleActivity
import little.goose.account.ui.account.analysis.AnalysisFragmentViewModel.Companion.MONTH
import little.goose.account.utils.getRealDate
import little.goose.account.utils.getRealMonth

class AnalysisBalanceRcvViewHolder(
    val binding: ItemAnalysisBalanceBinding,
    val timeType: Int
) : RecyclerView.ViewHolder(binding.root) {

    fun bindView(transactionBalance: TransactionBalance) {
        binding.root.setOnClickListener {
            TransactionExampleActivity.open(
                binding.root.context,
                transactionBalance.time,
                if (timeType == MONTH) {
                    TimeType.DATE
                } else TimeType.YEAR_MONTH
            )
        }

        binding.tvOne.text = if (timeType == MONTH) {
            transactionBalance.time.getRealDate().toString()
        } else transactionBalance.time.getRealMonth().toString()
        binding.tvTwo.text = transactionBalance.income.toPlainString()
        binding.tvThree.text = transactionBalance.expense.abs().toPlainString()
        binding.tvFour.text = transactionBalance.balance.toPlainString()
    }

}