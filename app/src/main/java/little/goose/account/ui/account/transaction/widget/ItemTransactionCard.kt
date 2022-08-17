package little.goose.account.ui.account.transaction.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.card.MaterialCardView
import little.goose.account.R
import little.goose.account.databinding.ItemTransactionBinding
import little.goose.account.logic.data.constant.AccountConstant.EXPENSE
import little.goose.account.logic.data.constant.AccountConstant.INCOME
import little.goose.account.logic.data.entities.Transaction
import little.goose.account.logic.data.models.TransactionIcon
import little.goose.account.ui.account.transaction.icon.TransactionIconHelper

class ItemTransactionCard : MaterialCardView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    private var binding: ItemTransactionBinding

    init {
        val view = inflate(context, R.layout.item_transaction, this)
        binding = ItemTransactionBinding.bind(view)
    }

    @SuppressLint("SetTextI18n")
    fun bindData(transaction: Transaction) {
        binding.apply {
            ivTransaction.setImageResource(TransactionIconHelper.getIconPath(transaction.icon_id))
            when(transaction.type) {
                EXPENSE -> tvMoney.text = transaction.money.toPlainString()
                INCOME -> tvMoney.text = "+" + transaction.money.toPlainString()
            }
            tvTransaction.text = transaction.content
        }
    }

    fun setMoney(money: String) {
        binding.tvMoney.text = money
    }

    fun setIcon(transactionIcon: TransactionIcon) {
        binding.apply {
            ivTransaction.setImageResource(transactionIcon.path)
            tvTransaction.text = transactionIcon.name
        }
    }

    fun setSelect(flag: Boolean) {
        if (flag) {
            binding.ivBgVector.visibility = View.VISIBLE
        } else {
            binding.ivBgVector.visibility = View.GONE
        }
    }

}