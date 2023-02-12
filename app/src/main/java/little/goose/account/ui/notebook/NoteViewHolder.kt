package little.goose.account.ui.notebook

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import little.goose.common.MultipleChoseHandler
import little.goose.account.databinding.ItemNoteBinding
import little.goose.account.logic.data.entities.Note
import little.goose.common.utils.toChineseMonthDayTime

class NoteViewHolder(
    private val binding: ItemNoteBinding,
    private val multipleChoseHandler: MultipleChoseHandler<Note>?
) : RecyclerView.ViewHolder(binding.root) {

    fun bindData(note: Note) {
        binding.apply {
            initSelect(note)
            if (note.title.isEmpty()) {
                tvNoteTitle.visibility = View.GONE
            } else {
                tvNoteTitle.visibility = View.VISIBLE
                tvNoteTitle.text = note.title
            }
            tvNoteContent.fromHtml(note.content)
            tvNoteDate.text = note.time.toChineseMonthDayTime()
        }
    }

    private fun initSelect(note: Note) {
        //fixme 需要优化
        multipleChoseHandler?.itemList?.contains(note)?.let { setSelect(it) }
    }

    fun setSelect(flag: Boolean) {
        binding.ivBgVector.visibility = if (flag) View.VISIBLE else View.GONE
    }
}