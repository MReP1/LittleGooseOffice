package little.goose.account.ui.notebook

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import little.goose.account.R
import little.goose.account.common.ItemSelectCallback
import little.goose.account.common.MultipleChoseHandler
import little.goose.account.common.dialog.NormalDialogFragment
import little.goose.account.databinding.FragmentNotebookBinding
import little.goose.account.logic.NoteRepository
import little.goose.account.logic.data.constant.NOTEBOOK
import little.goose.account.logic.data.entities.Note
import little.goose.account.appScope
import little.goose.account.ui.base.BaseFragment
import little.goose.account.ui.decoration.ItemGridLayoutDecoration
import little.goose.account.ui.notebook.note.NoteActivity
import little.goose.account.ui.notebook.note.NoteHelper
import little.goose.account.ui.search.SearchActivity
import little.goose.account.utils.*

@SuppressLint("NotifyDataSetChanged")
class NotebookFragment : BaseFragment(R.layout.fragment_notebook) {

    private val binding by viewBinding(FragmentNotebookBinding::bind)

    private val viewModel:NotebookViewModel by viewModels()

    private val multipleChoseHandler = MultipleChoseHandler<Note>()

    private val addOnclickListener = View.OnClickListener {
        NoteActivity.openAdd(requireContext())
    }
    private val deleteOnClickListener = View.OnClickListener {
        NormalDialogFragment.Builder()
            .setContent(getString(R.string.confirm_delete))
            .setConfirmCallback {
                viewLifecycleOwner.lifecycleScope.launch {
                    val list = multipleChoseHandler.deleteItemList()
                    binding.root.showSnackbar(R.string.deleted, 1000, R.string.undo) {
                        appScope.launch {
                            NoteRepository.addNoteList(list)
                        }
                    }
                }
            }
            .showNow(parentFragmentManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initActionBar()
        setRecyclerView()
        initFlow()
        initMultipleChose()
    }

    private fun initActionBar() {
        binding.homeActionBar.actionBarTitle.text = getString(R.string.notebook)
    }

    private val noteCallback = object : ItemSelectCallback<Note> {
        override fun onItemClick(item: Note) {
            NoteActivity.openEdit(requireContext(), item)
        }

        override fun onItemLongClick(item: Note) {}
    }

    private fun setRecyclerView() {
        binding.rcvNotebook.apply {
            addItemDecoration(ItemGridLayoutDecoration(dp16, 18.dp(), 18.dp()))
            adapter =
                NotebookRcvAdapter(NoteHelper.getNoteList(), noteCallback, multipleChoseHandler)
            layoutManager = GridLayoutManager(this@NotebookFragment.requireContext(), 2)
        }
    }

    private fun initFlow() {
        launchAndRepeatWithViewLifeCycle {
            viewModel.getAllNoteAsFlow().collect {
                (binding.rcvNotebook.adapter as? NotebookRcvAdapter)?.updateData(it)
                NoteHelper.setNoteList(it)
            }
        }
    }

    private fun initMultipleChose() {
        binding.apply {
            multiButton.apply {
                setOnFloatButtonClickListener(addOnclickListener)
                setOnFloatAllClickListener {
                    multipleChoseHandler.clearItemList()
                    multipleChoseHandler.addList(NoteHelper.getNoteList())
                    rcvNotebook.adapter?.notifyDataSetChanged()
                }
                setOnFloatVectorClickListener { cancelMultiChose() }
                setOnBackPressListener { cancelMultiChose() }
                setOnFloatSideClickListener { SearchActivity.open(requireContext(), NOTEBOOK) }
            }
        }
        launchAndRepeatWithViewLifeCycle {
            multipleChoseHandler.isMultipleChose.collect { isMulti ->
                binding.apply {
                    if (isMulti) {
                        homeActionBar.actionBarTitle.text = getString(R.string.multiple_chose)
                        multiButton.showDelete()
                        multiButton.setOnFloatButtonClickListener(deleteOnClickListener)
                    } else {
                        homeActionBar.actionBarTitle.text = getString(R.string.notebook)
                        multiButton.hideDelete()
                        multiButton.setOnFloatButtonClickListener(addOnclickListener)
                    }
                }
            }
        }
    }

    private fun cancelMultiChose() {
        multipleChoseHandler.release()
        binding.rcvNotebook.adapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        if (multipleChoseHandler.needRefresh) {
            binding.rcvNotebook.adapter?.notifyDataSetChanged()
        }
    }

    override fun onPause() {
        super.onPause()
        multipleChoseHandler.setNeedRefresh()
    }

    companion object {
        fun newInstance(): NotebookFragment {
            return NotebookFragment()
        }
    }
}