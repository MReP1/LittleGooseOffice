package little.goose.account.ui.transaction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import little.goose.design.system.theme.AccountTheme
import java.util.*

@AndroidEntryPoint
class TransactionActivity : AppCompatActivity() {

    companion object {
        private const val TRANSACTION = "transaction"
        private const val KEY_TIME = "time"

        fun openAdd(context: Context, date: Date? = null) {
            val intent = Intent(context, TransactionActivity::class.java)
            date?.let { intent.putExtra(KEY_TIME, it.time) }
            context.startActivity(intent)
        }

        fun openEdit(
            context: Context,
            transaction: little.goose.account.data.entities.Transaction
        ) {
            val intent = Intent(context, TransactionActivity::class.java).apply {
                putExtra(TRANSACTION, transaction)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccountTheme {
                TransactionScreen(
                    modifier = Modifier.fillMaxSize(),
                    onFinished = ::finish
                )
            }
        }
    }

}