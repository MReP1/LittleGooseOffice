package little.goose.note.ui.note

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnAttach
import dagger.hilt.android.AndroidEntryPoint
import little.goose.common.constants.TYPE_ADD
import little.goose.common.constants.TYPE_MODIFY
import little.goose.common.utils.collectLastWithLifecycle
import little.goose.common.utils.viewBinding
import little.goose.note.R
import little.goose.note.data.constants.KEY_NOTE
import little.goose.note.data.entities.Note
import little.goose.note.databinding.ActivityNoteBinding
import java.util.*
import kotlin.math.abs

@AndroidEntryPoint
class NoteActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityNoteBinding::inflate)
    private val viewModel by viewModels<NoteViewModel>()

    private var isBold = false
    private var isItalic = false
    private var isUnderline = false
    private var isStrikeThrough = false
    private val isBoldItalic get() = isBold && isItalic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTransparentStyle(binding.root, window)
        setContentView(binding.root)
        initNote()
        initView()
    }

    private fun initNote() {
        viewModel.note.collectLastWithLifecycle(lifecycle) { note ->
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
        when (viewModel.type) {
            TYPE_ADD -> viewModel.insertDatabase()
            TYPE_MODIFY -> viewModel.updateDatabase()
        }
    }

    private fun updateNote() {
        val newNote = viewModel.note.value.copy(
            title = binding.etTitle.text.toString(),
            content = binding.retContent.toHtml(),
            time = Date()
        )
        viewModel.setNote(newNote)
    }

    private fun setTransparentStyle(
        view: View,
        window: Window,
        isLightTheme: Boolean = true
    ) {
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)
        view.doOnAttach {
            setInsertContentTheme(window, view, isLightTheme)
            setInsertPadding(window, view)
        }
    }

    private fun setInsertContentTheme(
        window: Window,
        view: View,
        isLightTheme: Boolean
    ) {
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = isLightTheme
            isAppearanceLightNavigationBars = isLightTheme
        }
    }

    private fun setInsertPadding(window: Window, view: View) {
        val rootWindowInsert = ViewCompat.getRootWindowInsets(window.decorView) ?: return
        val statusInsert = rootWindowInsert.getInsets(WindowInsetsCompat.Type.statusBars())
        val paddingTop = abs(statusInsert.top - statusInsert.bottom)
        val navInsert = rootWindowInsert.getInsets(WindowInsetsCompat.Type.navigationBars())
        val paddingBottom = abs(navInsert.top - navInsert.bottom)
        if (paddingTop != 0 || paddingBottom != 0) {
            view.setPadding(0, paddingTop, 0, paddingBottom)
        }
    }

    companion object {
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