package little.goose.note.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import little.goose.common.decoration.ItemGridLayoutDecoration
import little.goose.note.ui.note.NoteActivity
import little.goose.account.utils.*
import little.goose.common.ItemSelectCallback
import little.goose.common.MultipleChoseHandler
import little.goose.common.dialog.NormalDialogFragment
import little.goose.common.utils.*
import little.goose.note.R
import little.goose.note.databinding.FragmentNotebookBinding

@SuppressLint("NotifyDataSetChanged")
@AndroidEntryPoint
class NotebookFragment
private constructor() : Fragment(R.layout.fragment_notebook) {

    private val binding by viewBinding(FragmentNotebookBinding::bind)

    private val viewModel: NotebookViewModel by viewModels()

    private val multipleChoseHandler = MultipleChoseHandler<little.goose.note.data.entities.Note>()

    private val addOnclickListener = View.OnClickListener {
        NoteActivity.openAdd(requireContext())
    }

    private val deleteOnClickListener = View.OnClickListener {
        NormalDialogFragment.Builder()
            .setContent(getString(little.goose.common.R.string.confirm_delete))
            .setConfirmCallback {
                viewLifecycleOwner.lifecycleScope.launch {
                    multipleChoseHandler.deleteItemList {
                        viewModel.deleteNoteList(it)
                        binding.root.showSnackbar(
                            little.goose.common.R.string.deleted,
                            1000,
                            little.goose.common.R.string.undo
                        ) {
                            viewModel.addNoteList(it)
                        }
                    }
                }
            }
            .showNow(parentFragmentManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        initFlow()
        initMultipleChose()
    }

    private val noteCallback = object : ItemSelectCallback<little.goose.note.data.entities.Note> {
        override fun onItemClick(item: little.goose.note.data.entities.Note) {
            NoteActivity.openEdit(requireContext(), item)
        }

        override fun onItemLongClick(item: little.goose.note.data.entities.Note) {}
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
                        multiButton.showDelete()
                        multiButton.setOnFloatButtonClickListener(deleteOnClickListener)
                    } else {
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