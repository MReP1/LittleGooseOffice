package little.goose.account.ui.account.transaction

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.launch
import little.goose.account.AccountApplication
import little.goose.account.R
import little.goose.account.common.dialog.NormalDialogFragment
import little.goose.account.databinding.LayoutDialogTransactionBinding
import little.goose.account.logic.AccountRepository
import little.goose.account.logic.data.constant.KEY_DELETE_ITEM
import little.goose.account.logic.data.constant.KEY_TRANSACTION
import little.goose.account.logic.data.constant.NOTIFY_DELETE_TRANSACTION
import little.goose.account.logic.data.entities.Transaction
import little.goose.account.ui.account.transaction.icon.TransactionIconHelper
import little.goose.account.utils.*
import java.math.BigDecimal
import java.util.*

class TransactionDialogFragment : DialogFragment(R.layout.layout_dialog_transaction) {

    private val binding by viewBinding(LayoutDialogTransactionBinding::bind)
    private lateinit var transaction: Transaction

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWindow()
        initView()
        initClick()
    }

    private fun initView() {
        transaction = arguments?.getParcelable(KEY_TRANSACTION)
            ?: Transaction(
                null, 0, BigDecimal(0), "null", "null", Date(), 1
            )
        binding.apply {
            ivIcon.setImageResource(TransactionIconHelper.getIconPath(transaction.icon_id))
            tvTitle.text = transaction.content
            tvMoney.text = transaction.money.toSignString()
            tvDescription.text = transaction.description
            tvTime.text = transaction.time.toChineseMonthDayTime()
        }
    }

    private fun initClick() {
        binding.btDelete.setOnClickListener {
            NormalDialogFragment.Builder()
                .setContent(getString(R.string.confirm_delete))
                .setConfirmCallback {
                    AccountApplication.supervisorScope.launch {
                        AccountRepository.deleteTransaction(transaction)
                        sendDeleteBroadcast()
                    }
                }
                .setCancelCallback {  }
                .showNow(parentFragmentManager)
            this.dismiss()
        }
        binding.btEdit.setOnClickListener {
            TransactionActivity.openEdit(requireContext(), transaction)
            this.dismiss()
        }
    }

    private fun initWindow() {
        dialog?.window?.setBackgroundDrawable(null)
        dialog?.window?.attributes?.apply {
            width = UIUtils.getWidthPercentPixel(0.72F)
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            gravity = Gravity.CENTER
        }
    }

    private fun sendDeleteBroadcast() {
        val intent = Intent(NOTIFY_DELETE_TRANSACTION).apply {
            setPackage(`package`)
            putExtra(KEY_DELETE_ITEM, transaction)
        }
        localBroadcastManager.sendBroadcast(intent)
    }

    companion object {
        fun newInstance(transaction: Transaction): TransactionDialogFragment {
            val bundle = Bundle().apply {
                putParcelable(KEY_TRANSACTION, transaction)
            }
            return TransactionDialogFragment().apply { arguments = bundle }
        }
    }
}