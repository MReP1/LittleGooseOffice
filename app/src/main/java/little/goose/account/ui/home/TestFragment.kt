package little.goose.account.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.launch
import little.goose.account.AccountApplication
import little.goose.account.R
import little.goose.account.logic.AccountRepository
import little.goose.account.logic.data.constant.AccountConstant.EXPENSE
import little.goose.account.logic.data.entities.Transaction
import little.goose.account.ui.account.transaction.icon.TransactionIconHelper
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class TestFragment : Fragment(R.layout.fragment_test) {

    companion object {
        fun getInstance(): TestFragment {
            return TestFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val root = view.findViewById<ScrollView>(R.id.root)
        root.setBackgroundColor(
            ContextCompat.getColor(this.requireContext(), R.color.background_color)
        )

        val button: Button = view.findViewById(R.id.test_one)
        button.setOnClickListener {
            AccountApplication.supervisorScope.launch {
                val transactionList = ArrayList<Transaction>()
                repeat(100) {
                    val type = getRandomZeroOrOne()
                    val iconId = if (type == EXPENSE) {
                        getRandomInt(1, 10)
                    } else {
                        getRandomInt(11, 13)
                    }
                    val money = if (type == EXPENSE) {
                        getRandomBigDecimal().negate()
                    } else {
                        getRandomBigDecimal()
                    }
                    transactionList.add(
                        Transaction(
                            null, type, money,
                            TransactionIconHelper.getIconName(iconId), "",
                            Date(), iconId
                        )
                    )
                }
                AccountRepository.addTransactionList(transactionList)
            }
        }

        val button2: Button = view.findViewById(R.id.test_two)
        button2.setOnClickListener {
            AccountApplication.supervisorScope.launch {

            }
        }
    }

    private fun getRandomZeroOrOne() = if (Random.nextBoolean()) {
        1
    } else {
        0
    }

    private fun getRandomBigDecimal(start: Int = 1, end: Int = 100): BigDecimal {
        return BigDecimal(Random.nextInt(start, end))
    }

    private fun getRandomInt(start: Int, end: Int): Int {
        return Random.nextInt(start, end)
    }

}