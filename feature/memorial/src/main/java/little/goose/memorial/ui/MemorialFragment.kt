package little.goose.memorial.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import little.goose.account.utils.*
import little.goose.common.MultipleChoseHandler
import little.goose.common.constants.NOTIFY_DELETE_MEMORIAL
import little.goose.common.dialog.NormalDialogFragment
import little.goose.design.system.theme.AccountTheme
import little.goose.memorial.data.constants.KEY_MEMORIAL
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.ui.widget.MemorialColumn
import little.goose.memorial.ui.widget.MemorialTitle

@SuppressLint("NotifyDataSetChanged")
@AndroidEntryPoint
class MemorialFragment : Fragment() {

    private val viewModel: MemorialFragmentViewModel by viewModels()

    private val multipleChoseHandler = MultipleChoseHandler<Memorial>()

    private val deleteListener = View.OnClickListener {
        NormalDialogFragment.Builder()
            .setContent(getString(little.goose.common.R.string.confirm_delete))
            .setConfirmCallback {
                viewLifecycleOwner.lifecycleScope.launch {
                    multipleChoseHandler.deleteItemList {
//                        MemorialRepository.deleteMemorials(it)
//                        binding.root.showSnackbar(R.string.deleted, 1000, R.string.undo) {
//                            lifecycleScope.launch { MemorialRepository.addMemorials(list) }
//                        }
                    }
                }
            }.showNow(parentFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.deleteReceiver.register(
            requireContext(),
            lifecycle,
            NOTIFY_DELETE_MEMORIAL
        ) { _, memorial ->
//            binding.root.showSnackbar(R.string.deleted, 1000, R.string.undo) {
//                lifecycleScope.launch {
//                    MemorialRepository.addMemorial(memorial)
//                }
//            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AccountTheme {
                    MemorialFragmentRoute(
                        modifier = Modifier.fillMaxSize(),
                        onMemorialClick = {
                            MemorialDialogFragment.newInstance(it)
                                .showNow(parentFragmentManager, KEY_MEMORIAL)
                        }
                    )
                }
            }
        }
    }

    private fun initView() {
        initFlowButton()
        initMultiChose()
    }

    private fun initFlowButton() {
//        binding.multiButton.apply {
//            setOnFloatButtonClickListener(openAddMemorial)
//            setOnFloatAllClickListener {
//                multipleChoseHandler.clearItemList()
//                multipleChoseHandler.addList(viewModel.memorials.value)
//                binding.rcvMemorial.adapter?.notifyDataSetChanged()
//            }
//            setOnFloatVectorClickListener { cancelMultiChose() }
//            setOnBackPressListener { cancelMultiChose() }
//            setOnFloatSideClickListener {
//                SearchActivity.open(requireContext(), MEMORIAL)
//            }
//        }
    }

    private fun initMultiChose() {
//        launchAndRepeatWithViewLifeCycle {
//            multipleChoseHandler.isMultipleChose.collect { isMulti ->
//                binding.apply {
//                    if (isMulti) {
//                        homeActionBar.actionBarTitle.text = getString(R.string.multiple_chose)
//                        multiButton.showDelete()
//                        multiButton.setOnFloatButtonClickListener(deleteListener)
//                    } else {
//                        homeActionBar.actionBarTitle.text = getString(R.string.memorial)
//                        multiButton.hideDelete()
//                        multiButton.setOnFloatButtonClickListener(openAddMemorial)
//                    }
//                }
//            }
//        }
    }

    private fun cancelMultiChose() {
        multipleChoseHandler.release()
    }


    override fun onPause() {
        super.onPause()
        multipleChoseHandler.setNeedRefresh()
    }

    companion object {
        fun newInstance() = MemorialFragment()
    }
}

@Composable
private fun MemorialFragmentRoute(
    modifier: Modifier,
    onMemorialClick: (Memorial) -> Unit
) {
    val viewModel: MemorialFragmentViewModel = hiltViewModel()
    val memorials by viewModel.memorials.collectAsState()
    val topMemorial by viewModel.topMemorial.collectAsState()
    MemorialFragmentScreen(
        modifier = modifier,
        memorials = memorials,
        topMemorial = topMemorial,
        onMemorialClick = onMemorialClick
    )
}

@Composable
private fun MemorialFragmentScreen(
    modifier: Modifier,
    memorials: List<Memorial>,
    topMemorial: Memorial?,
    onMemorialClick: (Memorial) -> Unit
) {
    Surface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (topMemorial != null) {
                MemorialTitle(
                    modifier = Modifier
                        .height(130.dp)
                        .fillMaxWidth(),
                    memorial = topMemorial
                )
            }
            MemorialColumn(
                memorials = memorials,
                onMemorialClick = onMemorialClick
            )
        }
    }
}