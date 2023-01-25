package little.goose.account.ui.memorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import little.goose.account.databinding.ActivityMemorialShowBinding
import little.goose.account.logic.data.constant.KEY_MEMORIAL
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.ui.base.BaseActivity
import little.goose.account.utils.parcelable

class MemorialShowActivity : BaseActivity() {

    private lateinit var binding: ActivityMemorialShowBinding

    private val viewModel: MemorialShowViewModel by viewModels()

    private val requestEditMemorial =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.memorial =
                result.data?.parcelable(KEY_MEMORIAL) ?: return@registerForActivityResult
            viewModel.memorial?.let { binding.memoCard.setMemorial(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemorialShowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
    }

    private fun initData() {
        viewModel.memorial = intent.parcelable(KEY_MEMORIAL) ?: run {
            finish()
            return
        }
    }

    private fun initView() {
        binding.apply {
            viewModel.memorial?.let { binding.memoCard.setMemorial(it) }
            actionBar.setOnBackClickListener { finish() }
            floatButton.setOnFloatButtonClickListener {
                val intent = viewModel.memorial?.let { memorial ->
                    MemorialActivity.getEditIntent(this@MemorialShowActivity, memorial)
                }
                requestEditMemorial.launch(intent)
            }
        }
    }

    companion object {
        fun open(context: Context, memorial: Memorial) {
            val intent = Intent(context, MemorialShowActivity::class.java).apply {
                putExtra(KEY_MEMORIAL, memorial)
            }
            context.startActivity(intent)
        }
    }

}