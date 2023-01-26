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
import little.goose.account.common.ItemSelectCallback
import little.goose.account.common.MultipleChoseHandler
import little.goose.account.common.dialog.NormalDialogFragment
import little.goose.account.databinding.FragmentScheduleBinding
import little.goose.account.logic.ScheduleRepository
import little.goose.account.logic.data.constant.KEY_SCHEDULE
import little.goose.account.logic.data.constant.NOTIFY_DELETE_SCHEDULE
import little.goose.account.logic.data.constant.NOTIFY_UPDATE_SCHEDULE
import little.goose.account.logic.data.constant.SCHEDULE
import little.goose.account.logic.data.entities.Schedule
import little.goose.account.ui.base.BaseFragment
import little.goose.account.ui.decoration.ItemLinearLayoutDecoration
import little.goose.account.ui.search.SearchActivity
import little.goose.account.utils.*

@SuppressLint("NotifyDataSetChanged")
class ScheduleFragment : BaseFragment(R.layout.fragment_schedule) {

    private val binding by viewBinding(FragmentScheduleBinding::bind)
    private val scheduleViewModel: ScheduleViewModel by viewModels()
    private var callback: ItemSelectCallback<Schedule>? = null
    private val multipleChoseHandler = MultipleChoseHandler<Schedule>()

    companion object {
        fun newInstance(): ScheduleFragment {
            return ScheduleFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleViewModel.deleteReceiver.register(
            lifecycle = lifecycle,
            action = NOTIFY_DELETE_SCHEDULE
        ) { _, schedule ->
            binding.root.showSnackbar(R.string.deleted, 1000, R.string.undo) {
                appScope.launch { ScheduleRepository.addSchedule(schedule) }
            }
        }

        scheduleViewModel.updateReceiver.register(
            lifecycle = lifecycle,
            action = NOTIFY_UPDATE_SCHEDULE
        ) { _, _ ->
            refreshRecyclerView()
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
                    val list = multipleChoseHandler.deleteItemList()
                    binding.root.showSnackbar(R.string.deleted, 1000, R.string.undo) {
                        appScope.launch { ScheduleRepository.addScheduleList(list) }
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
                    multipleChoseHandler.addList(ScheduleHelper.scheduleList)
                    rcvSchedule.adapter?.notifyDataSetChanged()
                }
                setOnFloatVectorClickListener { cancelMultiChose() }
                setOnBackPressListener { cancelMultiChose() }
                setOnFloatSideClickListener { SearchActivity.open(requireContext(), SCHEDULE) }
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
            adapter =
                ScheduleRcvAdapter(ScheduleHelper.scheduleList, callback, multipleChoseHandler)
            addItemDecoration(ItemLinearLayoutDecoration(dp16, 18.dp(), 10.dp()))
            layoutManager = LinearLayoutManager(requireContext())

            //观察数据变化
            launchAndRepeatWithViewLifeCycle {
                scheduleViewModel.getAllScheduleState().collect {
                    updateRcvData(it)
                }
            }
        }
    }

    private fun refreshRecyclerView() {
        viewLifecycleOwner.lifecycleScope.launch {
            updateRcvData(ScheduleRepository.getAllSchedule())
        }
    }

    private fun cancelMultiChose() {
        multipleChoseHandler.release()
        binding.rcvSchedule.adapter?.notifyDataSetChanged()
    }

    private fun updateRcvData(list: List<Schedule>) {
        ScheduleHelper.scheduleList = list
        (binding.rcvSchedule.adapter as? ScheduleRcvAdapter)?.updateData(list)
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