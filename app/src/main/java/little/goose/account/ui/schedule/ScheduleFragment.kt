package little.goose.account.ui.schedule

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import little.goose.account.R
import little.goose.account.appScope
import little.goose.account.databinding.FragmentScheduleBinding
import little.goose.account.logic.ScheduleRepository
import little.goose.account.logic.data.constant.KEY_SCHEDULE
import little.goose.account.logic.data.entities.Schedule
import little.goose.account.ui.base.BaseFragment
import little.goose.account.ui.decoration.ItemLinearLayoutDecoration
import little.goose.account.utils.*
import little.goose.common.ItemSelectCallback
import little.goose.common.MultipleChoseHandler
import little.goose.common.constants.NOTIFY_DELETE_SCHEDULE
import little.goose.common.constants.NOTIFY_UPDATE_SCHEDULE
import little.goose.common.dialog.NormalDialogFragment

@SuppressLint("NotifyDataSetChanged")
class ScheduleFragment
private constructor() : BaseFragment(R.layout.fragment_schedule) {

    private val binding by viewBinding(FragmentScheduleBinding::bind)
    private val viewModel: ScheduleViewModel by viewModels()
    private var callback: ItemSelectCallback<Schedule>? = null
    private val multipleChoseHandler = MultipleChoseHandler<Schedule>()

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
            binding.root.showSnackbar(R.string.deleted, 1000, R.string.undo) {
                appScope.launch { ScheduleRepository.addSchedule(schedule) }
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
        initActionBar()
        initFloatButton()
        initMultipleChose()
    }

    private fun initActionBar() {
        binding.homeActionBar.actionBarTitle.text = getString(R.string.schedule)
    }

    private val openAddListener = View.OnClickListener {
        ScheduleDialogFragment.newInstance().showNow(parentFragmentManager, "add_schedule")
    }
    private val deleteListener = View.OnClickListener {
        //删除
        NormalDialogFragment.Builder()
            .setContent(getString(R.string.confirm_delete))
            .setConfirmCallback {
                viewLifecycleOwner.lifecycleScope.launch {
                    multipleChoseHandler.deleteItemList {
                        ScheduleRepository.deleteSchedules(it)
                        binding.root.showSnackbar(R.string.deleted, 1000, R.string.undo) {
                            appScope.launch { ScheduleRepository.addSchedules(it) }
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
                        homeActionBar.actionBarTitle.text = getString(R.string.multiple_chose)
                        multiButton.showDelete()
                        multiButton.setOnFloatButtonClickListener(deleteListener)
                    } else {
                        homeActionBar.actionBarTitle.text = getString(R.string.schedule)
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
            adapter = ScheduleRcvAdapter(viewModel.schedules.value, callback, multipleChoseHandler)
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