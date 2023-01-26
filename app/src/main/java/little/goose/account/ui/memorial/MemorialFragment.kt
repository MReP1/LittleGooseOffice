package little.goose.account.ui.memorial

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.account.R
import little.goose.account.appScope
import little.goose.account.common.ItemClickCallback
import little.goose.account.common.MultipleChoseHandler
import little.goose.account.common.dialog.NormalDialogFragment
import little.goose.account.databinding.FragmentMemorialBinding
import little.goose.account.logic.MemorialRepository
import little.goose.account.logic.data.constant.KEY_MEMORIAL
import little.goose.account.logic.data.constant.MEMORIAL
import little.goose.account.logic.data.constant.NOTIFY_DELETE_MEMORIAL
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.ui.base.BaseFragment
import little.goose.account.ui.decoration.ItemLinearLayoutDecoration
import little.goose.account.ui.search.SearchActivity
import little.goose.account.utils.*

@SuppressLint("NotifyDataSetChanged")
class MemorialFragment : BaseFragment(R.layout.fragment_memorial) {

    private val viewModel: MemorialFragmentViewModel by viewModels()

    private val binding by viewBinding(FragmentMemorialBinding::bind)

    private val multipleChoseHandler = MultipleChoseHandler<Memorial>()

    private val openAddMemorial = View.OnClickListener {
        MemorialActivity.openAdd(requireContext())
    }

    private val deleteListener = View.OnClickListener {
        NormalDialogFragment.Builder()
            .setContent(getString(R.string.confirm_delete))
            .setConfirmCallback {
                viewLifecycleOwner.lifecycleScope.launch {
                    val list = multipleChoseHandler.deleteItemList()
                    binding.root.showSnackbar(R.string.deleted, 1000, R.string.undo) {
                        appScope.launch { MemorialRepository.addMemorials(list) }
                    }
                }
            }.showNow(parentFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.deleteReceiver.register(lifecycle, NOTIFY_DELETE_MEMORIAL) { _, memorial ->
            if (memorial.isTop) initHeader()
            binding.root.showSnackbar(R.string.deleted, 1000, R.string.undo) {
                appScope.launch {
                    MemorialRepository.addMemorial(memorial)
                    if (memorial.isTop) {
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                            initHeader()
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        binding.apply {
            rcvMemorial.apply {
                adapter = MemorialRcvAdapter(
                    list = viewModel.memorials.value,
                    multipleChoseHandler = multipleChoseHandler,
                    callback = object : ItemClickCallback<Memorial> {
                        override fun onItemClick(item: Memorial) {
                            MemorialDialogFragment.newInstance(item)
                                .showNow(parentFragmentManager, KEY_MEMORIAL)
                        }
                    }
                )
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(ItemLinearLayoutDecoration(16.dp(), 16.dp(), 12.dp()))
            }
        }
        initFlowButton()
        initMultiChose()
        initHeader()
    }

    private fun initHeader() {
        viewModel.topMemorial.collectLastWithLifecycleOwner(viewLifecycleOwner) { memorial ->
            setMemorialHeader(memorial)
        }
    }

    private fun setMemorialHeader(memorial: Memorial?) {
        memorial?.let {
            binding.cardHeader.visibility = View.VISIBLE
            binding.cardHeader.setMemorial(it)
        } ?: run { binding.cardHeader.visibility = View.GONE }
    }

    private fun initFlowButton() {
        binding.multiButton.apply {
            setOnFloatButtonClickListener(openAddMemorial)
            setOnFloatAllClickListener {
                multipleChoseHandler.clearItemList()
                multipleChoseHandler.addList(viewModel.memorials.value)
                binding.rcvMemorial.adapter?.notifyDataSetChanged()
            }
            setOnFloatVectorClickListener { cancelMultiChose() }
            setOnBackPressListener { cancelMultiChose() }
            setOnFloatSideClickListener {
                SearchActivity.open(requireContext(), MEMORIAL)
            }
        }
    }

    private fun initMultiChose() {
        launchAndRepeatWithViewLifeCycle {
            multipleChoseHandler.isMultipleChose.collect { isMulti ->
                binding.apply {
                    if (isMulti) {
                        homeActionBar.actionBarTitle.text = getString(R.string.multiple_chose)
                        multiButton.showDelete()
                        multiButton.setOnFloatButtonClickListener(deleteListener)
                    } else {
                        homeActionBar.actionBarTitle.text = getString(R.string.memorial)
                        multiButton.hideDelete()
                        multiButton.setOnFloatButtonClickListener(openAddMemorial)
                    }
                }
            }
        }
    }

    private fun initData() {
        viewModel.memorials.collectLastWithLifecycleOwner(viewLifecycleOwner) {
            (binding.rcvMemorial.adapter as MemorialRcvAdapter).updateData(it)
        }
    }

    private fun cancelMultiChose() {
        multipleChoseHandler.release()
        binding.rcvMemorial.adapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        if (multipleChoseHandler.needRefresh) {
            binding.rcvMemorial.adapter?.notifyDataSetChanged()
        }
    }

    override fun onPause() {
        super.onPause()
        multipleChoseHandler.setNeedRefresh()
    }

    companion object {
        fun newInstance() = MemorialFragment()
    }
}