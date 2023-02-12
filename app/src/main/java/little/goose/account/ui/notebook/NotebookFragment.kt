package little.goose.account.ui.notebook

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import little.goose.account.R
import little.goose.account.appScope
import little.goose.account.databinding.FragmentNotebookBinding
import little.goose.account.logic.NoteRepository
import little.goose.account.logic.data.entities.Note
import little.goose.common.decoration.ItemGridLayoutDecoration
import little.goose.account.ui.notebook.note.NoteActivity
import little.goose.account.utils.*
import little.goose.common.ItemSelectCallback
import little.goose.common.MultipleChoseHandler
import little.goose.common.dialog.NormalDialogFragment
import little.goose.common.utils.*

@SuppressLint("NotifyDataSetChanged")
class NotebookFragment
private constructor() : Fragment(R.layout.fragment_notebook) {

    private val binding by viewBinding(FragmentNotebookBinding::bind)

    private val viewModel: NotebookViewModel by viewModels()

    private val multipleChoseHandler = MultipleChoseHandler<Note>()

    private val addOnclickListener = View.OnClickListener {
        NoteActivity.openAdd(requireContext())
    }

    private val deleteOnClickListener = View.OnClickListener {
        NormalDialogFragment.Builder()
            .setContent(getString(little.goose.common.R.string.confirm_delete))
            .setConfirmCallback {
                viewLifecycleOwner.lifecycleScope.launch {
                    multipleChoseHandler.deleteItemList {
                        NoteRepository.deleteNoteList(it)
                        binding.root.showSnackbar(
                            little.goose.common.R.string.deleted,
                            1000,
                            little.goose.common.R.string.undo
                        ) {
                            appScope.launch {
                                NoteRepository.addNoteList(it)
                            }
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
            adapter = NotebookRcvAdapter(viewModel.notes.value, noteCallback, multipleChoseHandler)
            layoutManager = GridLayoutManager(this@NotebookFragment.requireContext(), 2)
        }
    }

    private fun initFlow() {
        viewModel.notes.collectLastWithLifecycleOwner(viewLifecycleOwner) {
            (binding.rcvNotebook.adapter as? NotebookRcvAdapter)?.updateData(it)
        }
    }

    private fun initMultipleChose() {
        binding.apply {
            multiButton.apply {
                setOnFloatButtonClickListener(addOnclickListener)
                setOnFloatAllClickListener {
                    multipleChoseHandler.clearItemList()
                    multipleChoseHandler.addList(viewModel.notes.value)
                    rcvNotebook.adapter?.notifyDataSetChanged()
                }
                setOnFloatVectorClickListener { cancelMultiChose() }
                setOnBackPressListener { cancelMultiChose() }
                setOnFloatSideClickListener {
//                    SearchActivity.open(requireContext(), NOTEBOOK)
                }
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