package little.goose.account.ui.memorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import little.goose.account.common.dialog.InputTextDialogFragment
import little.goose.account.common.dialog.time.DateTimePickerBottomDialog
import little.goose.account.common.dialog.time.TimeType
import little.goose.account.databinding.ActivityMemorialBinding
import little.goose.account.logic.MemorialRepository
import little.goose.account.logic.data.constant.KEY_MEMORIAL
import little.goose.account.logic.data.constant.KEY_TYPE
import little.goose.account.logic.data.constant.TYPE_ADD
import little.goose.account.logic.data.constant.TYPE_MODIFY
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.superScope
import little.goose.account.ui.base.BaseActivity
import little.goose.account.utils.appendTimeSuffix
import little.goose.account.utils.toChineseYearMonDayWeek

class MemorialActivity : BaseActivity() {

    private lateinit var binding: ActivityMemorialBinding
    private val viewModel: MemorialActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemorialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
    }

    private fun initData() {
        viewModel.type = intent.getStringExtra(KEY_TYPE) ?: TYPE_ADD
        viewModel.memorial = intent.getParcelableExtra(KEY_MEMORIAL) ?: return
        viewModel.memorial.also {
            lifecycleScope.launch {
                viewModel.content.emit(it.content)
                viewModel.time.emit(it.time)
                binding.swToTop.isChecked = it.isTop
            }
        }
    }

    private fun initView() {
        initDataListener()
        binding.apply {
            binding.actionBar.setOnBackClickListener { finish() }
            btChangeTime.setOnClickListener {
                DateTimePickerBottomDialog.Builder()
                    .setDimVisibility(true)
                    .setTime(viewModel.memorial.time)
                    .setType(TimeType.DATE)
                    .setConfirmAction {
                        lifecycleScope.launch { viewModel.time.emit(it) }
                    }.showNow(supportFragmentManager)
            }
            flContent.setOnClickListener {
                InputTextDialogFragment.Builder()
                    .setInputText(viewModel.memorial.content)
                    .setConfirmCallback {
                        lifecycleScope.launch { viewModel.content.emit(it) }
                    }.showNow(supportFragmentManager)
            }
            btConfirm.setOnClickListener {
                viewModel.storeMemorial()
                setResult(71, Intent().putExtra(KEY_MEMORIAL, viewModel.memorial))
                finish()
            }
            swToTop.setOnCheckedChangeListener { _, isTop ->
                viewModel.isChangeTop = true
                viewModel.memorial.isTop = isTop
            }
        }
    }

    private fun initDataListener() {
        binding.apply {
            lifecycleScope.launch {
                viewModel.content.collect { content ->
                    tvContent.text = content
                    actionBar.setTitle(content)
                }
            }
            lifecycleScope.launch {
                viewModel.time.collect { time ->
                    tvOriTime.text = time.toChineseYearMonDayWeek()
                    val title = viewModel.content.value.appendTimeSuffix(time)
                    actionBar.setTitle(title)
                    tvMemoTime.setTime(time)
                }
            }
        }
    }

    companion object {

        fun getEditIntent(context: Context, memorial: Memorial): Intent {
            return Intent(context, MemorialActivity::class.java).apply {
                putExtra(KEY_TYPE, TYPE_MODIFY)
                putExtra(KEY_MEMORIAL, memorial)
            }
        }

        fun openAdd(context: Context) {
            val intent = Intent(context, MemorialActivity::class.java).apply {
                putExtra(KEY_TYPE, TYPE_ADD)
            }
            context.startActivity(intent)
        }
    }

}