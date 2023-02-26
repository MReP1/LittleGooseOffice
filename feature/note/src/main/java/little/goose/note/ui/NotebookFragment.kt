package little.goose.note.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import little.goose.account.utils.*
import little.goose.common.ItemSelectCallback
import little.goose.common.MultipleChoseHandler
import little.goose.common.decoration.ItemGridLayoutDecoration
import little.goose.common.dialog.NormalDialogFragment
import little.goose.common.utils.*
import little.goose.note.R
import little.goose.note.data.entities.Note
import little.goose.note.databinding.FragmentNotebookBinding
import little.goose.note.ui.note.NoteActivity
import middle.goose.richtext.RichTextView

@SuppressLint("NotifyDataSetChanged")
@AndroidEntryPoint
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

@Composable
fun NoteBookRoute(
    modifier: Modifier = Modifier,
    onNoteClick: (Note) -> Unit
) {
    val viewModel = viewModel<NotebookViewModel>()
    val notes by viewModel.notes.collectAsState()
    NoteBookScreen(modifier = modifier, notes = notes, onNoteClick)
}

@Composable
fun NoteBookScreen(
    modifier: Modifier = Modifier,
    notes: List<Note>,
    onNoteClick: (Note) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        content = {
            items(
                items = notes,
                key = { it.id ?: -1 }
            ) { note ->
                NoteItem(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(120.dp),
                    note = note,
                    onNoteClick = onNoteClick
                )
            }
        }
    )
}

@Composable
fun NoteItem(
    modifier: Modifier = Modifier,
    note: Note,
    onNoteClick: (Note) -> Unit
) {
    Card(
        modifier = modifier,
        onClick = {
            onNoteClick(note)
        }
    ) {
        Column(
            modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
        ) {
            Text(text = note.title)
            AndroidView(
                factory = { RichTextView(it) },
                modifier = Modifier.weight(1f)
            ) { textureView ->
                textureView.fromHtml(note.content)
            }
        }
    }
}