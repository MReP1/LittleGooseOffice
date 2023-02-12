package little.goose.account.ui.account.transaction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import little.goose.account.R
import little.goose.account.appScope
import little.goose.account.databinding.ActivityAddTransactionBinding
import little.goose.account.databinding.ItemIconCardBinding
import little.goose.account.databinding.LayoutAddTransactionBinding
import little.goose.account.logic.data.constant.AccountConstant.EXPENSE
import little.goose.account.logic.data.constant.AccountConstant.INCOME
import little.goose.account.logic.data.entities.Transaction
import little.goose.account.logic.data.models.TransactionIcon
import little.goose.account.ui.account.transaction.icon.TransactionIconHelper
import little.goose.common.dialog.DateTimePickerBottomDialog
import little.goose.common.dialog.InputTextDialogFragment
import little.goose.common.utils.SnackbarUtils
import little.goose.common.utils.parcelable
import little.goose.common.utils.toChineseMonthDayTime
import little.goose.common.utils.viewBinding
import java.math.BigDecimal
import java.util.*

@AndroidEntryPoint
class TransactionActivity : AppCompatActivity(),
    View.OnClickListener, TransactionViewModel.ButtonCallback {

    private val binding by viewBinding(ActivityAddTransactionBinding::inflate)
    private val viewModel by lazy {
        ViewModelProvider(this)[TransactionViewModel::class.java]
    }

    companion object {
        private const val TRANSACTION = "transaction"
        private const val KEY_TIME = "time"

        fun openAdd(context: Context, date: Date? = null) {
            val intent = Intent(context, TransactionActivity::class.java)
            date?.let { intent.putExtra(KEY_TIME, it.time) }
            context.startActivity(intent)
        }

        fun openEdit(context: Context, transaction: Transaction) {
            val intent = Intent(context, TransactionActivity::class.java).apply {
                putExtra(TRANSACTION, transaction)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        initType()
        updateDate()
        updateDescription()
    }

    private fun initView() {
        initViewPager()
        initTabLayout()
        initNumberTable()
        initCallback()
    }

    private fun initType() {
        val transaction: Transaction? = intent.parcelable(TRANSACTION)
        transaction?.let {
            viewModel.apply {
                setIconSelectedId(it.icon_id, it.type)
                type = TransactionViewModel.EDIT
                when (it.type) {
                    EXPENSE -> {
                        listTransaction = listOf(
                            Transaction(
                                it.id, EXPENSE,
                                it.money, it.content, it.description, it.time, it.icon_id
                            ),
                            Transaction(
                                it.id, INCOME,
                                it.money, TransactionIconHelper.getIconName(11),
                                it.description, it.time, 11
                            )
                        )
                    }
                    INCOME -> {
                        listTransaction = listOf(
                            Transaction(
                                it.id, EXPENSE,
                                it.money, TransactionIconHelper.getIconName(1),
                                it.description, it.time, 1
                            ),
                            Transaction(
                                it.id, INCOME,
                                it.money, it.content, it.description, it.time, it.icon_id
                            )
                        )
                    }
                }
                moneyCleanAndSet(it.money)
            }
            if (it.type == INCOME) {
                binding.transactionViewPager.setCurrentItem(1, false)
            }
        } ?: run {
            val time = intent.getLongExtra(KEY_TIME, 0L)
            if (time > 0L) {
                val date = Date(time)
                viewModel.listTransaction.forEach {
                    it.time = date
                }
            }
        }
    }

    private fun initCallback() {
        viewModel.setCallback(this)
    }

    private fun initTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.transactionViewPager) { tab, position ->
            when (position) {
                EXPENSE -> tab.text = getString(R.string.expense)
                INCOME -> tab.text = getString(R.string.income)
            }
        }.attach()
    }

    private fun initViewPager() {
        lifecycleScope.launch {
            binding.transactionViewPager.apply {
                adapter = TransactionPagerAdapter(
                    listOf(
                        TransactionIconHelper.expenseIconList,
                        TransactionIconHelper.incomeIconList
                    )
                )
                //获取目前所在页面位置
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        viewModel.position = position
                    }
                })
                //去除滑动尽头的效果
                getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
            }
        }
    }

    private fun updateDate() {
        binding.tvDate.apply {
            text = viewModel.listTransaction[0].time.toChineseMonthDayTime()
        }
    }

    private fun updateDescription() {
        binding.tvDescription.apply {
            text = viewModel.listTransaction[0].description.ifBlank {
                getString(R.string.transaction_description)
            }
            setOnClickListener {
                InputTextDialogFragment.Builder()
                    .setInputText(viewModel.listTransaction[0].description)
                    .setConfirmCallback {
                        text = it
                        viewModel.listTransaction[0].description = it
                        viewModel.listTransaction[1].description = it
                    }
                    .showNow(supportFragmentManager)
            }
        }
    }

    override fun doneCallback() {
        //完成
        if (binding.buttonDone.text == getString(R.string.finish)) {
            if (viewModel.listTransaction[0].money == BigDecimal(0)) {
                SnackbarUtils.showNormalMessage(binding.root, getString(R.string.money_cant_be_zero))
                return
            }
            appScope.launch {
                viewModel.updateDatabase()
            }
            finish()
        }
    }

    override fun allButtonCallback() {
        //刷新右下角按键
        if (viewModel.isContainsOperation()) {
            binding.buttonDone.text = "="
        } else {
            binding.buttonDone.text = getString(R.string.finish)
        }
    }

    override fun againCallback(isZero: Boolean) {
        //下一笔
        if (isZero) {
            SnackbarUtils.showNormalMessage(binding.root, getString(R.string.money_cant_be_zero))
        } else {
            updateDescription()
            updateDate()
        }
    }

    private fun initNumberTable() {
        this.also { activity ->
            binding.apply {
                numOne.setOnClickListener(activity)
                numTwo.setOnClickListener(activity)
                numThree.setOnClickListener(activity)
                numFour.setOnClickListener(activity)
                numFive.setOnClickListener(activity)
                numSix.setOnClickListener(activity)
                numSeven.setOnClickListener(activity)
                numEight.setOnClickListener(activity)
                numNine.setOnClickListener(activity)
                numZero.setOnClickListener(activity)
                numDot.setOnClickListener(activity)
                buttonPlus.setOnClickListener(activity)
                buttonDone.setOnClickListener(activity)
                buttonSub.setOnClickListener(activity)
                buttonBackspace.setOnClickListener(activity)
                buttonAgain.setOnClickListener(activity)
                iconBack.setOnClickListener(activity)
                tvDescription.setOnClickListener(activity)
                tvDate.setOnClickListener { openDate() }
            }
        }
    }

    override fun onClick(view: View) {
        binding.apply {
            when (view) {
                iconBack -> finish()
                numOne -> viewModel.appendMoneyEnd('1')
                numTwo -> viewModel.appendMoneyEnd('2')
                numThree -> viewModel.appendMoneyEnd('3')
                numFour -> viewModel.appendMoneyEnd('4')
                numFive -> viewModel.appendMoneyEnd('5')
                numSix -> viewModel.appendMoneyEnd('6')
                numSeven -> viewModel.appendMoneyEnd('7')
                numEight -> viewModel.appendMoneyEnd('8')
                numNine -> viewModel.appendMoneyEnd('9')
                numZero -> viewModel.appendMoneyEnd('0')
                numDot -> viewModel.modifyOther(TransactionViewModel.DOT)
                buttonPlus -> viewModel.modifyOther(TransactionViewModel.PLUS)
                buttonSub -> viewModel.modifyOther(TransactionViewModel.SUB)
                buttonBackspace -> viewModel.modifyOther(TransactionViewModel.BACKSPACE)
                buttonDone -> viewModel.modifyOther(TransactionViewModel.DONE)
                buttonAgain -> viewModel.modifyOther(TransactionViewModel.AGAIN)
            }
        }
    }

    private fun openDate() {
        DateTimePickerBottomDialog.Builder()
            .setTime(viewModel.listTransaction[0].time)
            .setConfirmAction {
                viewModel.listTransaction[0].time = it
                viewModel.listTransaction[1].time = it
                updateDate()
            }
            .showNow(supportFragmentManager)
    }

    //ViewPager Adapter
    inner class TransactionPagerAdapter(
        private val iconListList: List<List<TransactionIcon>>
    ) : RecyclerView.Adapter<TransactionPagerHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionPagerHolder {
            val binding = LayoutAddTransactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return TransactionPagerHolder(binding)
        }

        override fun onBindViewHolder(holder: TransactionPagerHolder, position: Int) {
            holder.bindIcon(iconListList[position])
            holder.bindTransaction(position)
        }

        override fun getItemCount(): Int = iconListList.size

    }

    interface OnIconClickListener {
        fun onIconClick(icon: TransactionIcon)
    }

    //ViewPager Holder
    inner class TransactionPagerHolder(private val binding: LayoutAddTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var pos: Int = 0

        fun bindIcon(iconList: List<TransactionIcon>) {
            binding.iconRecyclerView.apply {
                //Icon点击监听
                adapter = IconRcvAdapter(iconList, object : OnIconClickListener {
                    override fun onIconClick(icon: TransactionIcon) {
                        viewModel.listTransaction[pos].apply {
                            icon_id = icon.id
                            content = icon.name
                        }
                        binding.itemTransaction.setIcon(icon)
                        viewModel.setIconSelectedId(icon.id, icon.type)
                    }
                })
                layoutManager = GridLayoutManager(context, 5, RecyclerView.VERTICAL, true)
            }
        }

        fun bindTransaction(position: Int) {
            pos = position
            binding.apply {
                itemTransaction.bindData(viewModel.listTransaction[pos])
                lifecycleScope.launch {
                    //金钱监听
                    viewModel.moneyStateFlow.collect {
                        if (!it.contains('+') && it.lastIndexOf('-') <= 0 && it.last() != '.') {
                            val money = viewModel.filtrateZero(it)
                            viewModel.listTransaction[pos].money = BigDecimal(money)
                        }
                        binding.itemTransaction.setMoney(it)
                    }
                }
            }
        }
    }

    //IconAdapter
    inner class IconRcvAdapter(
        private val iconList: List<TransactionIcon>,
        private val onIconClickListener: OnIconClickListener
    ) : RecyclerView.Adapter<IconRcvHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconRcvHolder {
            val binding =
                ItemIconCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return IconRcvHolder(binding)
        }

        override fun onBindViewHolder(holder: IconRcvHolder, position: Int) {
            holder.bindData(iconList[position + 1])
            holder.itemView.setOnClickListener {
                onIconClickListener.onIconClick(iconList[position + 1])
            }
        }

        //iconList多一个ID为0的占位icon
        override fun getItemCount(): Int = iconList.size - 1
    }

    //IconHolder
    inner class IconRcvHolder(private val binding: ItemIconCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(transactionIcon: TransactionIcon) {
            binding.icon.setImageResource(transactionIcon.path)
            lifecycleScope.launch {
                when (transactionIcon.type) {
                    EXPENSE -> viewModel.expenseIconStateFlow.collect {
                        if (it == transactionIcon.id) {
                            binding.iconCard.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    this@TransactionActivity,
                                    R.color.item_selected
                                )
                            )
                        } else {
                            binding.iconCard.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    this@TransactionActivity,
                                    R.color.primary_color
                                )
                            )
                        }
                    }
                    INCOME -> viewModel.incomeIconStateFlow.collect {
                        if (it == transactionIcon.id) {
                            binding.iconCard.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    this@TransactionActivity,
                                    R.color.item_selected
                                )
                            )
                        } else {
                            binding.iconCard.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    this@TransactionActivity,
                                    R.color.primary_color
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}