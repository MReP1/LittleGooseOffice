package little.goose.account.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import little.goose.account.R
import little.goose.account.appScope
import little.goose.account.common.ItemClickCallback
import little.goose.account.common.ItemSelectCallback
import little.goose.account.databinding.ActivityScheduleSearchBinding
import little.goose.account.logic.AccountRepository
import little.goose.account.logic.MemorialRepository
import little.goose.account.logic.ScheduleRepository
import little.goose.account.logic.data.constant.*
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.logic.data.entities.Note
import little.goose.account.logic.data.entities.Schedule
import little.goose.account.logic.data.entities.Transaction
import little.goose.account.ui.account.AccountRcvAdapter
import little.goose.account.ui.account.transaction.TransactionDialogFragment
import little.goose.account.ui.base.BaseActivity
import little.goose.account.ui.decoration.ItemGridLayoutDecoration
import little.goose.account.ui.decoration.ItemLinearLayoutDecoration
import little.goose.account.ui.memorial.MemorialDialogFragment
import little.goose.account.ui.memorial.MemorialRcvAdapter
import little.goose.account.ui.notebook.NotebookRcvAdapter
import little.goose.account.ui.notebook.note.NoteActivity
import little.goose.account.ui.schedule.ScheduleDialogFragment
import little.goose.account.ui.schedule.ScheduleRcvAdapter
import little.goose.account.utils.*

@AndroidEntryPoint
class SearchActivity : BaseActivity() {

    private val binding by viewBinding(ActivityScheduleSearchBinding::inflate)

    /** ----------- 存放 RcvAdapter ------------*/
    private lateinit var scheduleRcvAdapter: ScheduleRcvAdapter
    private lateinit var transactionRcvAdapter: AccountRcvAdapter
    private lateinit var notebookRcvAdapter: NotebookRcvAdapter
    private lateinit var memorialRcvAdapter: MemorialRcvAdapter

    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this)[SearchActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initType()
        initView()
    }

    private fun initView() {
        showSearch()
        binding.apply {
            multiButton.setOnFloatButtonClickListener { showSearch() }
        }
    }

    private fun showSearch() {
        binding.etSearch.apply {
            requestFocus()
            post { KeyBoard.show(binding.etSearch) }
        }
    }

    private fun initType() {
        when (val type = intent.getIntExtra(TYPE, 0)) {
            ACCOUNT -> {
                viewModel.type = type
                initRcvForAccount()
                initEditTextForAccount()
                registerTransactionReceiver()
            }
            SCHEDULE -> {
                viewModel.type = type
                initRcvForSchedule()
                initEditTextForSchedule()
                registerScheduleReceiver()
            }
            NOTEBOOK -> {
                viewModel.type = type
                initRcvForNote()
                initEditTextForNote()
            }
            MEMORIAL -> {
                viewModel.type = type
                initRcvForMemorial()
                initEditTextForMemorial()
                registerMemorialReceiver()
            }
            else -> finish()
        }
    }

    /**
     * ---------------------------------纪念日---------------------------------
     * */

    private fun initEditTextForMemorial() {
        binding.etSearch.addTextChangedListener {
            val content = it.toString()
            if (content.isNotEmpty()) {
                updateRcvMemorial(content)
            } else {
                memorialRcvAdapter.updateData(emptyList())
            }
        }
    }

    private fun updateRcvMemorial(content: String) {
        lifecycleScope.launch {
            memorialRcvAdapter.updateData(viewModel.searchMemorialList(content))
        }
    }

    private fun updateMemorialView() {
        val content = binding.etSearch.text.toString()
        if (content.isNotEmpty()) {
            updateRcvMemorial(content)
        }
    }

    private fun initRcvForMemorial() {
        val memorialCallback = object : ItemClickCallback<Memorial> {
            override fun onItemClick(item: Memorial) {
                MemorialDialogFragment.newInstance(item)
                    .showNow(supportFragmentManager, KEY_MEMORIAL)
            }
        }
        memorialRcvAdapter = MemorialRcvAdapter(emptyList(), callback = memorialCallback)
        binding.rcvResult.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            addItemDecoration(ItemLinearLayoutDecoration(16.dp(), 16.dp(), 12.dp()))
            adapter = memorialRcvAdapter
        }
    }

    private fun registerMemorialReceiver() {
        viewModel.memorialDeleteReceiver.register(
            lifecycle,
            NOTIFY_DELETE_MEMORIAL
        ) { _, memorial ->
            updateMemorialView()
            binding.root.showDeleteSnackbar {
                appScope.launch {
                    MemorialRepository.addMemorial(memorial)
                    updateMemorialView()
                }
            }
        }
    }


    /**
     * ---------------------------------账本---------------------------------
     * */
    private fun initEditTextForAccount() {
        binding.etSearch.addTextChangedListener {
            val content = it.toString()
            if (content.isNotEmpty()) {
                updateRcvAccount(content)
            } else {
                transactionRcvAdapter.updateData(emptyList())
            }
        }
    }

    private fun updateRcvAccount(content: String) {
        lifecycleScope.launch {
            transactionRcvAdapter.updateData(viewModel.searchTransactionList(content))
        }
    }

    private fun updateAccountView() {
        val content = binding.etSearch.text.toString()
        if (content.isNotEmpty()) {
            updateRcvAccount(content)
        }
    }

    private fun initRcvForAccount() {
        val accountCallback = object : ItemSelectCallback<Transaction> {
            override fun onItemClick(item: Transaction) {
                TransactionDialogFragment.showNow(item, supportFragmentManager)
            }

            override fun onItemLongClick(item: Transaction) {}
        }
        transactionRcvAdapter = AccountRcvAdapter(emptyList(), accountCallback)
        binding.rcvResult.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            addItemDecoration(ItemLinearLayoutDecoration(dp16, dp16, 10.dp()))
            adapter = transactionRcvAdapter
        }
    }

    private fun registerTransactionReceiver() {
        viewModel.transactionDeleteReceiver.register(
            lifecycle,
            NOTIFY_DELETE_TRANSACTION
        ) { _, transaction ->
            updateAccountView()
            binding.root.showSnackbar(R.string.deleted, 1000, R.string.undo) {
                appScope.launch {
                    AccountRepository.addTransaction(transaction)
                    updateAccountView()
                }
            }
        }
    }

    /**
     * ---------------------------------行程表---------------------------------
     * */
    private fun initEditTextForSchedule() {
        binding.etSearch.addTextChangedListener {
            val content = it.toString()
            if (content.isNotEmpty()) {
                updateRcvSchedule(content)
            } else {
                scheduleRcvAdapter.updateData(emptyList())
            }
        }
    }

    private fun initRcvForSchedule() {
        val scheduleCallback = object : ItemSelectCallback<Schedule> {
            override fun onItemClick(item: Schedule) {
                ScheduleDialogFragment.newInstance(item)
                    .showNow(supportFragmentManager, "schedule")
            }

            override fun onItemLongClick(item: Schedule) {}
        }
        scheduleRcvAdapter = ScheduleRcvAdapter(emptyList(), scheduleCallback)
        binding.rcvResult.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            addItemDecoration(ItemLinearLayoutDecoration(dp16, 18.dp(), 10.dp()))
            adapter = scheduleRcvAdapter
        }
    }

    private fun updateRcvSchedule(content: String) {
        lifecycleScope.launch {
            scheduleRcvAdapter.updateData(viewModel.searchScheduleList(content))
        }
    }

    //获取EditText的内容来Update
    private fun updateScheduleView() {
        val content = binding.etSearch.text.toString()
        if (content.isNotEmpty()) {
            updateRcvSchedule(content)
        }
    }

    private fun registerScheduleReceiver() {
        viewModel.scheduleUpdateReceiver.register(lifecycle, NOTIFY_UPDATE_SCHEDULE) { _, _ ->
            updateScheduleView()
        }
        viewModel.scheduleDeleteReceiver.register(
            lifecycle,
            NOTIFY_DELETE_SCHEDULE
        ) { _, schedule ->
            updateScheduleView()
            binding.root.showSnackbar(R.string.deleted, 1000, R.string.undo) {
                appScope.launch {
                    ScheduleRepository.addSchedule(schedule)
                    updateScheduleView()
                }
            }
        }
    }

    /**
     * ----------------------------备忘录---------------------------------
     * */
    private fun initEditTextForNote() {
        binding.etSearch.addTextChangedListener {
            val content = it.toString()
            if (content.isNotEmpty()) {
                updateRcvNoteBook(content)
            } else {
                notebookRcvAdapter.updateData(emptyList())
            }
        }
    }

    private fun updateRcvNoteBook(content: String) {
        lifecycleScope.launch {
            notebookRcvAdapter.updateData(viewModel.searchNoteList(content))
        }
    }

    private fun initRcvForNote() {
        val noteCallback = object : ItemSelectCallback<Note> {
            override fun onItemClick(item: Note) {
                NoteActivity.openEdit(this@SearchActivity, item)
            }

            override fun onItemLongClick(item: Note) {}
        }
        notebookRcvAdapter = NotebookRcvAdapter(emptyList(), noteCallback)
        binding.rcvResult.apply {
            addItemDecoration(ItemGridLayoutDecoration(dp16, 18.dp(), 18.dp()))
            layoutManager = GridLayoutManager(this@SearchActivity, 2)
            adapter = notebookRcvAdapter
        }
    }

    /** ---------------------------- */

    override fun onStart() {
        super.onStart()
        updateRcvData()
    }

    private fun updateRcvData() {
        val content = binding.etSearch.text.toString()
        if (content.isNotEmpty()) {
            //因为Schedule不存在切到别的Activity所以不对Schedule执行刷新
            when (viewModel.type) {
                ACCOUNT -> {
                    updateRcvAccount(content)
                }
                NOTEBOOK -> {
                    updateRcvNoteBook(content)
                }
                MEMORIAL -> {
                    updateRcvMemorial(content)
                }
            }
        }
    }

    companion object {
        private const val TYPE = "type"
        fun open(context: Context, type: Int) {
            if (type == ACCOUNT || type == SCHEDULE
                || type == NOTEBOOK || type == MEMORIAL
            ) {
                val intent = Intent(context, SearchActivity::class.java).apply {
                    putExtra(TYPE, type)
                }
                context.startActivity(intent)
            }
        }
    }
}