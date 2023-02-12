package little.goose.account.ui.schedule

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import little.goose.account.R
import little.goose.common.dialog.NormalDialogFragment
import little.goose.common.dialog.DateTimePickerBottomDialog
import little.goose.account.databinding.LayoutCardScheduleBinding
import little.goose.account.logic.ScheduleRepository
import little.goose.account.logic.data.constant.*
import little.goose.account.logic.data.entities.Schedule
import little.goose.account.utils.*
import little.goose.common.constants.NOTIFY_DELETE_SCHEDULE
import little.goose.common.constants.NOTIFY_UPDATE_SCHEDULE
import little.goose.common.localBroadcastManager
import java.util.*

class ScheduleDialogFragment
private constructor() : DialogFragment(R.layout.layout_card_schedule) {

    private val binding by viewBinding(LayoutCardScheduleBinding::bind)
    private var schedule: Schedule? = null
    private var isModify = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWindow()
        initView()
    }

    private fun initView() {
        schedule = arguments?.parcelable(KEY_SCHEDULE)
        val time: Date? = arguments?.serializable(KEY_TIME)

        isModify = (schedule != null)
        if (isModify) {
            initModifyPage()
        } else {
            schedule = time?.let { Schedule(null, "", "", it) }
                ?: Schedule(null, "", "", Date())
            initAddPage()
        }
        binding.apply {
            buttonDate.text = schedule?.time?.toChineseStringWithYear()
            buttonDate.setOnClickListener {
                DateTimePickerBottomDialog.Builder()
                    .setTime(schedule?.time)
                    .setConfirmAction {
                        schedule?.time = it
                        buttonDate.text = it.toChineseStringWithYear()
                    }
                    .setDimVisibility(false)
                    .showNow(parentFragmentManager)
            }
        }

    }

    private fun initAddPage() {
        binding.apply {
            confirmButton.setOnClickListener {
                if (etScheduleTitle.text.toString().isBlank()) {
                    SnackbarUtils.showNormalMessage(
                        binding.root, getString(R.string.schedule_cant_be_blank)
                    )
                } else {
                    lifecycleScope.launch {
                        addSchedule()
                        dismiss()
                    }
                }
            }
            cancelButton.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun initModifyPage() {
        binding.apply {
            cardTitle.text = resources.getString(R.string.modify_schedule)
            etScheduleTitle.setText(schedule?.title)
            etScheduleContent.setText(schedule?.content)
            confirmButton.setOnClickListener {
                if (etScheduleTitle.text.toString().isBlank()) {
                    SnackbarUtils.showNormalMessage(
                        binding.root, getString(R.string.schedule_cant_be_blank)
                    )
                } else {
                    lifecycleScope.launch {
                        updateSchedule()
                        dismiss()
                    }
                }
            }
            cancelButton.text = getString(R.string.delete)
            cancelButton.setOnClickListener {
                NormalDialogFragment.Builder()
                    .setContent(getString(R.string.confirm_delete))
                    .setConfirmCallback {
                        lifecycleScope.launch {
                            schedule?.let { schedule -> ScheduleRepository.deleteSchedule(schedule) }
                            sendDeleteBroadcast()
                        }
                    }
                    .setCancelCallback { }
                    .showNow(parentFragmentManager)
                dismiss()
            }
        }
    }

    private suspend fun updateSchedule() {
        withContext(Dispatchers.IO) {
            updateData()
            schedule?.let { ScheduleRepository.updateSchedule(it) }
            //更新完返回
            KeyBoard.hide(binding.root)
            requireContext().localBroadcastManager.sendBroadcast(Intent(NOTIFY_UPDATE_SCHEDULE))
        }
    }

    private suspend fun addSchedule() {
        withContext(Dispatchers.IO) {
            updateData()
            schedule?.let { ScheduleRepository.addSchedule(it) }
        }
    }

    private fun sendDeleteBroadcast() {
        val intent = Intent(NOTIFY_DELETE_SCHEDULE).apply {
            setPackage(`package`)
            putExtra(KEY_DELETE_ITEM, schedule)
        }
        requireContext().localBroadcastManager.sendBroadcast(intent)
    }

    private fun updateData() {
        schedule?.apply {
            title = binding.etScheduleTitle.text.toString()
            content = binding.etScheduleContent.text.toString()
        }
    }

    private fun initWindow() {
        dialog?.window?.setBackgroundDrawable(null)
        dialog?.window?.attributes?.apply {
            width = UIUtils.getWidthPercentPixel(0.76F)
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            gravity = Gravity.CENTER
        }
    }

    companion object {
        fun newInstance(
            schedule: Schedule? = null,
            time: Date? = null
        ): ScheduleDialogFragment {
            return ScheduleDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_SCHEDULE, schedule)
                    putSerializable(KEY_TIME, time)
                }
            }
        }
    }

}