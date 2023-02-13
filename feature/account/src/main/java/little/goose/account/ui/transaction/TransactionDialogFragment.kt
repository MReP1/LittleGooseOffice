package little.goose.account.ui.transaction

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.launch
import little.goose.account.R
import little.goose.common.dialog.NormalDialogFragment
import little.goose.account.databinding.LayoutDialogTransactionBinding
import little.goose.common.constants.NOTIFY_DELETE_TRANSACTION
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import little.goose.common.constants.KEY_TRANSACTION
import little.goose.common.localBroadcastManager
import little.goose.common.utils.*
import java.math.BigDecimal
import java.util.*
import little.goose.account.data.entities.Transaction
import little.goose.account.logic.AccountRepository
import little.goose.common.commonScope
import little.goose.common.constants.KEY_DELETE_ITEM
import javax.inject.Inject

class TransactionDialogFragment : DialogFragment(R.layout.layout_dialog_transaction) {

    private val binding by viewBinding(LayoutDialogTransactionBinding::bind)
    private lateinit var transaction: Transaction

    @Inject lateinit var accountRepository: AccountRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWindow()
        initView()
        initClick()
    }

    private fun initView() {
        transaction = arguments?.parcelable(KEY_TRANSACTION)
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
                .setContent(getString(little.goose.common.R.string.confirm_delete))
                .setConfirmCallback {
                    commonScope.launch {
                        accountRepository.deleteTransaction(transaction)
                        sendDeleteBroadcast()
                    }
                }
                .setCancelCallback { }
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
        requireContext().localBroadcastManager.sendBroadcast(intent)
    }

    companion object {
        fun showNow(transaction: Transaction, fragmentManager: FragmentManager) {
            TransactionDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_TRANSACTION, transaction)
                }
            }.showNow(fragmentManager, KEY_TRANSACTION)
        }
    }
}