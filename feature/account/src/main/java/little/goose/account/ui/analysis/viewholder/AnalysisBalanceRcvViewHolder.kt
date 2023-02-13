package little.goose.account.ui.analysis.viewholder

import androidx.recyclerview.widget.RecyclerView
import little.goose.common.dialog.time.TimeType
import little.goose.account.databinding.ItemAnalysisBalanceBinding
import little.goose.account.ui.TransactionExampleActivity
import little.goose.account.ui.analysis.AnalysisFragmentViewModel.Companion.MONTH
import little.goose.common.utils.getRealDate
import little.goose.common.utils.getRealMonth

class AnalysisBalanceRcvViewHolder(
    val binding: ItemAnalysisBalanceBinding,
    val timeType: Int
) : RecyclerView.ViewHolder(binding.root) {

    fun bindView(transactionBalance: little.goose.account.data.models.TransactionBalance) {
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