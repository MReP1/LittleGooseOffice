package little.goose.account.ui.analysis

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import little.goose.design.system.theme.AccountTheme

@AndroidEntryPoint
class AccountAnalysisActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccountTheme {
                TransactionAnalysisScreen(
                    modifier = Modifier.fillMaxSize(),
                    onBack = ::finish
                )
            }
        }
    }

    companion object {
        fun open(context: Context) {
            val intent = Intent(context, AccountAnalysisActivity::class.java)
            context.startActivity(intent)
        }
    }
}