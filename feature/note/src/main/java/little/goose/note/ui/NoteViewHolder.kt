package little.goose.note.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import little.goose.common.MultipleChoseHandler
import little.goose.common.utils.toChineseMonthDayTime
import little.goose.note.databinding.ItemNoteBinding

class NoteViewHolder(
    private val binding: ItemNoteBinding,
    private val multipleChoseHandler: MultipleChoseHandler<little.goose.note.data.entities.Note>?
) : RecyclerView.ViewHolder(binding.root) {

    fun bindData(note: little.goose.note.data.entities.Note) {
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

    private fun initSelect(note: little.goose.note.data.entities.Note) {
        //fixme 需要优化
        multipleChoseHandler?.itemList?.contains(note)?.let { setSelect(it) }
    }

    fun setSelect(flag: Boolean) {
        binding.ivBgVector.visibility = if (flag) View.VISIBLE else View.GONE
    }
}