package little.goose.account.ui.notebook.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch
import little.goose.account.AccountApplication
import little.goose.account.R
import little.goose.account.databinding.ActivityNoteBinding
import little.goose.account.logic.data.entities.Note
import little.goose.account.ui.base.BaseActivity
import little.goose.account.utils.parcelable
import java.util.*

class NoteActivity : BaseActivity() {

    private lateinit var binding: ActivityNoteBinding
    private val viewModel: NoteViewModel by lazy {
        ViewModelProvider(this)[NoteViewModel::class.java]
    }
    private lateinit var note: Note
    private var type = ADD

    private var isBold = false
    private var isItalic = false
    private var isUnderline = false
    private var isStrikeThrough = false
    private val isBoldItalic get() = isBold && isItalic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNote()
        initView()
    }

    private fun initNote() {
        note = intent.parcelable(KEY_NOTE) ?: Note()
        note.id?.let {
            type = EDIT
            binding.etTitle.setText(note.title)
            binding.retContent.fromHtml(note.content)
        }
    }

    private fun initView() {
        initButton()
    }

    private fun initButton() {
        binding.apply {
            iconBack.setOnClickListener {
                finish()
            }
            flContent.setOnClickListener {
                retContent.setSelection(retContent.text!!.length)
                retContent.showSoftInput()
            }
            //加粗
            ivBold.setOnClickListener { setBold() }
            //斜体
            ivItalics.setOnClickListener { setItalic() }
            //划掉
            ivSweep.setOnClickListener { setStrikeThrough() }
            //下划线
            ivUnderline.setOnClickListener { setUnderline() }
            //引用
//            ivQuote.setOnClickListener { retContent.quote() }
            //列表
//            ivBullet.setOnClickListener { retContent.bullet() }
            //撤回
            ivUndo.setOnClickListener { retContent.undo() }
            //重做
            ivRedo.setOnClickListener { retContent.redo() }
        }
    }

    private fun setBold() {
        binding.apply {
            if (retContent.selectionEnd > retContent.selectionStart) {
                retContent.bold()
            } else {
                isBold = if (!isBold) {
                    retContent.setCurrentBold()
                    ivBold.setImageResource(R.drawable.icon_bold_red)
                    true
                } else {
                    if (isBoldItalic) {
                        retContent.setCurrentItalic()
                    } else {
                        retContent.setCurrentNormal()
                    }
                    ivBold.setImageResource(R.drawable.icon_bold_black)
                    false
                }
            }
        }
    }

    private fun setItalic() {
        binding.apply {
            if (retContent.selectionEnd > retContent.selectionStart) {
                retContent.italic()
            } else {
                isItalic = if (!isItalic) {
                    retContent.setCurrentItalic()
                    ivItalics.setImageResource(R.drawable.icon_italics_red)
                    true
                } else {
                    if (isBoldItalic) {
                        retContent.setCurrentBold()
                    } else {
                        retContent.setCurrentNormal()
                    }
                    ivItalics.setImageResource(R.drawable.icon_italics_black)
                    false
                }
            }
        }
    }

    private fun setUnderline() {
        binding.apply {
            if (retContent.selectionEnd > retContent.selectionStart) {
                retContent.underline()
            } else {
                if (!isUnderline) {
                    isUnderline = true
                    retContent.setCurrentUnderLine(true)
                    ivUnderline.setImageResource(R.drawable.icon_underline_red)
                } else {
                    isUnderline = false
                    retContent.setCurrentUnderLine(false)
                    ivUnderline.setImageResource(R.drawable.icon_underline_black)
                }
            }
        }
    }

    private fun setStrikeThrough() {
        binding.apply {
            if (retContent.selectionEnd > retContent.selectionStart) {
                retContent.strikethrough()
            } else {
                if (!isStrikeThrough) {
                    isStrikeThrough = true
                    retContent.setCurrentStrikeThrough(true)
                    ivSweep.setImageResource(R.drawable.icon_sweep_red)
                } else {
                    isStrikeThrough = false
                    retContent.setCurrentStrikeThrough(false)
                    ivSweep.setImageResource(R.drawable.icon_sweep_black)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        updateNote()
        when (type) {
            ADD -> insertDatabase()
            EDIT -> updateDatabase()
        }
    }

    private fun updateNote() {
        note.apply {
            title = binding.etTitle.text.toString()
            content = binding.retContent.toHtml()
            time = Date()
        }
    }

    private fun insertDatabase() {
        if (!isNoteBlank()) {
            AccountApplication.supervisorScope.launch {
                val id = viewModel.insertNote(note)
                if (!this@NoteActivity.isDestroyed) {
                    note.id = id
                    type = EDIT
                }
            }
        }
    }

    private fun updateDatabase() {
        if (!isNoteBlank()) {
            viewModel.updateNote(note)
        } else {
            viewModel.deleteNote(note)
        }
    }

    private fun isNoteBlank() = note.title.isBlank() && note.content.isBlank()

    companion object {
        private const val KEY_NOTE = "note"
        private const val ADD = 0
        private const val EDIT = 1

        fun openAdd(context: Context) {
            val intent = Intent(context, NoteActivity::class.java)
            context.startActivity(intent)
        }

        fun openEdit(context: Context, note: Note) {
            val intent = Intent(context, NoteActivity::class.java).apply {
                putExtra(KEY_NOTE, note)
            }
            context.startActivity(intent)
        }
    }

}