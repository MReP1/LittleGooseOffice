package little.goose.account.ui.memorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import little.goose.account.databinding.ActivityMemorialShowBinding
import little.goose.account.logic.data.constant.KEY_MEMORIAL
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.ui.base.BaseActivity
import little.goose.account.utils.collectLastWithLifecycle
import little.goose.account.utils.parcelable
import little.goose.account.utils.viewBinding

@AndroidEntryPoint
class MemorialShowActivity : BaseActivity() {

    private val binding: ActivityMemorialShowBinding by viewBinding(ActivityMemorialShowBinding::inflate)

    private val viewModel: MemorialShowViewModel by viewModels()

    private val requestEditMemorial =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val memorial = result.data?.parcelable<Memorial>(KEY_MEMORIAL)
                ?: return@registerForActivityResult
            viewModel.updateMemorial(memorial)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.actionBar.setOnBackClickListener { finish() }
        binding.floatButton.setOnFloatButtonClickListener {
            val intent = MemorialActivity.getEditIntent(
                this@MemorialShowActivity, viewModel.memorial.value
            )
            requestEditMemorial.launch(intent)
        }
        viewModel.memorial.collectLastWithLifecycle(lifecycle) { memorial ->
            binding.memoCard.setMemorial(memorial)
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