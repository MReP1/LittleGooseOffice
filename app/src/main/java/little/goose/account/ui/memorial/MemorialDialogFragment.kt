package little.goose.account.ui.memorial

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.launch
import little.goose.account.R
import little.goose.account.common.dialog.NormalDialogFragment
import little.goose.account.databinding.LayoutDialogMemorialBinding
import little.goose.account.logic.MemorialRepository
import little.goose.account.logic.data.constant.KEY_DELETE_ITEM
import little.goose.account.logic.data.constant.KEY_MEMORIAL
import little.goose.account.logic.data.constant.NOTIFY_DELETE_MEMORIAL
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.superScope
import little.goose.account.utils.*

class MemorialDialogFragment : DialogFragment() {

    private var _binding: LayoutDialogMemorialBinding? = null
    private val binding get() = _binding!!

    private lateinit var memorial: Memorial

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = LayoutDialogMemorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWindow()
        initView()
    }

    private fun initView() {
        memorial = arguments?.getParcelable(KEY_MEMORIAL) ?: Memorial(null, "null")
        binding.apply {
            tvContent.text = memorial.content.appendTimeSuffix(memorial.time)
            tvMemoTime.setTime(memorial.time)
            tvOriTime.text = memorial.time.toChineseYearMonDayWeek().appendTimePrefix(memorial.time)

            btModify.setOnClickListener {
                MemorialShowActivity.open(requireContext(), memorial)
                dismiss()
            }
            btDelete.setOnClickListener {
                NormalDialogFragment.Builder()
                    .setContent(requireContext().getString(R.string.confirm_delete))
                    .setConfirmCallback {
                        superScope.launch {
                            MemorialRepository.deleteMemorial(memorial)
                            sendDeleteBroadcast()
                        }
                    }.showNow(parentFragmentManager)
                dismiss()
            }
        }
    }

    private fun initWindow() {
        dialog?.window?.setBackgroundDrawable(null)
        dialog?.window?.attributes?.apply {
            width = UIUtils.getWidthPercentPixel(0.78F)
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            gravity = Gravity.CENTER
        }
    }

    private fun sendDeleteBroadcast() {
        val intent = Intent(NOTIFY_DELETE_MEMORIAL).apply {
            setPackage(`package`)
            putExtra(KEY_DELETE_ITEM, memorial)
        }
        localBroadcastManager.sendBroadcast(intent)
    }

    companion object {
        fun newInstance(memorial: Memorial): MemorialDialogFragment {
            val bundle = Bundle().also { it.putParcelable(KEY_MEMORIAL, memorial) }
            return MemorialDialogFragment().apply { arguments = bundle }
        }
    }
}