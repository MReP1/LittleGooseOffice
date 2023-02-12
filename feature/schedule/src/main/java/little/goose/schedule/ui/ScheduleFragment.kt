package little.goose.schedule.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import little.goose.common.utils.collectLastWithLifecycleOwner
import little.goose.common.utils.launchAndRepeatWithViewLifeCycle
import little.goose.common.ItemSelectCallback
import little.goose.common.MultipleChoseHandler
import little.goose.common.constants.KEY_SCHEDULE
import little.goose.common.constants.NOTIFY_DELETE_SCHEDULE
import little.goose.common.constants.NOTIFY_UPDATE_SCHEDULE
import little.goose.common.decoration.ItemLinearLayoutDecoration
import little.goose.common.dialog.NormalDialogFragment
import little.goose.common.utils.dp
import little.goose.common.utils.dp16
import little.goose.common.utils.showSnackbar
import little.goose.common.utils.viewBinding
import little.goose.schedule.R
import little.goose.schedule.databinding.FragmentScheduleBinding
import little.goose.schedule.data.entities.Schedule

@SuppressLint("NotifyDataSetChanged")
@AndroidEntryPoint
class ScheduleFragment
private constructor() : Fragment(R.layout.fragment_schedule) {

    private val binding by viewBinding(FragmentScheduleBinding::bind)
    private val viewModel: ScheduleViewModel by viewModels()
    private var callback: ItemSelectCallback<Schedule>? = null
    private val multipleChoseHandler =
        MultipleChoseHandler<Schedule>()

    companion object {
        fun newInstance(): ScheduleFragment {
            return ScheduleFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.deleteReceiver.register(
            context = requireContext(),
            lifecycle = lifecycle,
            action = NOTIFY_DELETE_SCHEDULE
        ) { _, schedule ->
            binding.root.showSnackbar(
                little.goose.common.R.string.deleted,
                1000,
                little.goose.common.R.string.undo
            ) {
                viewModel.addSchedule(schedule)
            }
        }

        viewModel.updateReceiver.register(
            context = requireContext(),
            lifecycle = lifecycle,
            action = NOTIFY_UPDATE_SCHEDULE
        ) { _, _ ->
            viewModel.updateSchedules()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        initFloatButton()
        initMultipleChose()
    }

    private val openAddListener = View.OnClickListener {
        ScheduleDialogFragment.newInstance().showNow(parentFragmentManager, "add_schedule")
    }
    private val deleteListener = View.OnClickListener {
        //删除
        NormalDialogFragment.Builder()
            .setContent(getString(little.goose.common.R.string.confirm_delete))
            .setConfirmCallback {
                viewLifecycleOwner.lifecycleScope.launch {
                    multipleChoseHandler.deleteItemList {
                        viewModel.deleteSchedules(it)
                        binding.root.showSnackbar(
                            little.goose.common.R.string.deleted,
                            1000,
                            little.goose.common.R.string.undo
                        ) {
                            viewModel.addSchedules(it)
                        }
                    }
                }
            }
            .showNow(parentFragmentManager)
    }

    private fun initFloatButton() {
        binding.apply {
            multiButton.apply {
                setOnFloatButtonClickListener(openAddListener)
                setOnFloatAllClickListener {
                    multipleChoseHandler.clearItemList()
                    multipleChoseHandler.addList(viewModel.schedules.value)
                    rcvSchedule.adapter?.notifyDataSetChanged()
                }
                setOnFloatVectorClickListener { cancelMultiChose() }
                setOnBackPressListener { cancelMultiChose() }
                setOnFloatSideClickListener {
//                    SearchActivity.open(requireContext(), SCHEDULE)
                }
            }
        }
    }

    private fun initMultipleChose() {
        launchAndRepeatWithViewLifeCycle {
            multipleChoseHandler.isMultipleChose.collect {
                //切换多选状态
                binding.apply {
                    if (it) {
                        multiButton.showDelete()
                        multiButton.setOnFloatButtonClickListener(deleteListener)
                    } else {
                        multiButton.hideDelete()
                        multiButton.setOnFloatButtonClickListener(openAddListener)
                    }
                }
            }
        }
    }

    private fun setRecyclerView() {
        setCallback()
        binding.rcvSchedule.apply {
            adapter = ScheduleRcvAdapter(
                viewModel.schedules.value,
                callback,
                multipleChoseHandler,
                updateSchedule = {
                    viewModel.updateSchedule(it)
                }
            )
            addItemDecoration(ItemLinearLayoutDecoration(dp16, 18.dp(), 10.dp()))
            layoutManager = LinearLayoutManager(requireContext())
            viewModel.schedules.collectLastWithLifecycleOwner(viewLifecycleOwner) {
                (binding.rcvSchedule.adapter as? ScheduleRcvAdapter)?.updateData(it)
            }
        }
    }

    private fun cancelMultiChose() {
        multipleChoseHandler.release()
        binding.rcvSchedule.adapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        if (multipleChoseHandler.needRefresh) {
            binding.rcvSchedule.adapter?.notifyDataSetChanged()
        }
    }

    override fun onPause() {
        super.onPause()
        multipleChoseHandler.setNeedRefresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback = null
    }

    private fun setCallback() {
        callback = object : ItemSelectCallback<Schedule> {
            override fun onItemClick(item: Schedule) {
                ScheduleDialogFragment.newInstance(item).show(parentFragmentManager, KEY_SCHEDULE)
            }

            override fun onItemLongClick(item: Schedule) {}
        }
    }
}