package little.goose.account.ui.notebook

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import little.goose.common.ItemSelectCallback
import little.goose.common.MultipleChoseHandler
import little.goose.account.databinding.ItemNoteBinding
import little.goose.account.logic.data.entities.Note

class NotebookRcvAdapter(
    private var list: List<Note>,
    private var callback: ItemSelectCallback<Note>? = null,
    private val multipleChoseHandler: MultipleChoseHandler<Note>? = null
) : RecyclerView.Adapter<NoteViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding =
            ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding, multipleChoseHandler)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = list[position]
        holder.bindData(note)
        holder.itemView.apply {
            setOnClickListener {
                if (multipleChoseHandler?.isMultipleChose?.value == true) {
                    multiClick(holder, note)
                } else {
                    callback?.onItemClick(note)
                }
            }
            setOnLongClickListener {
                if (multipleChoseHandler?.isMultipleChose?.value == false) {
                    multipleChoseHandler.ready()
                    multiClick(holder, note)
                }
                callback?.onItemLongClick(note)
                return@setOnLongClickListener true
            }
        }
    }

    private fun multiClick(holder: NoteViewHolder, note: Note) {
        multipleChoseHandler?.let { handler ->
            val flag = handler.clickItem(note)
            holder.setSelect(flag)
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<Note>) {
        this.list = list
        notifyDataSetChanged()
    }
}