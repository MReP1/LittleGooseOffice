package little.goose.account.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import little.goose.account.R
import little.goose.account.appScope
import little.goose.account.databinding.ItemHomeWidgetBinding
import little.goose.account.logic.ScheduleRepository
import little.goose.account.logic.data.entities.Schedule
import little.goose.account.logic.data.entities.Transaction
import little.goose.account.ui.account.transaction.TransactionDialogFragment
import little.goose.account.ui.account.transaction.icon.TransactionIconHelper
import little.goose.account.ui.schedule.ScheduleDialogFragment
import little.goose.account.utils.getRealTime
import little.goose.account.utils.toSignString

class HomeWidgetRcvAdapter(
    private var list: List<Any>,
    private var fragmentManager: FragmentManager
) : RecyclerView.Adapter<HomeWidgetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeWidgetViewHolder {
        val binding = ItemHomeWidgetBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HomeWidgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeWidgetViewHolder, position: Int) {
        when (val item = list[position]) {
            is Transaction -> {
                setTransactionHolder(holder, item)
            }
            is Schedule -> {
                setScheduleHolder(holder, item)
            }
            else -> {
                throw Exception("not support this format")
            }
        }
    }

    private fun setScheduleHolder(holder: HomeWidgetViewHolder, schedule: Schedule) {
        val drawableId = if (schedule.isfinish) {
            R.drawable.icon_checked_box
        } else {
            R.drawable.icon_uncheck_box
        }
        holder.setData(
            ItemHomeWidget(
                schedule.title, schedule.time.getRealTime(), drawableId
            )
        ) {
            appScope.launch(Dispatchers.Main) {
                schedule.isfinish = !schedule.isfinish
                updateSchedule(schedule)
                val tempDrawable = if (schedule.isfinish) {
                    R.drawable.icon_checked_box
                } else {
                    R.drawable.icon_uncheck_box
                }
                holder.updateImgResource(tempDrawable)
            }
        }
        holder.itemView.setOnClickListener {
            ScheduleDialogFragment.newInstance(schedule).showNow(fragmentManager, "schedule")
        }
    }

    private fun setTransactionHolder(holder: HomeWidgetViewHolder, transaction: Transaction) {
        holder.setData(
            ItemHomeWidget(
                transaction.content, transaction.money.toSignString(),
                TransactionIconHelper.getIconPath(transaction.icon_id)
            )
        )
        holder.itemView.setOnClickListener {
            TransactionDialogFragment.showNow(transaction, fragmentManager)
        }
    }

    private suspend fun updateSchedule(schedule: Schedule) {
        withContext(Dispatchers.IO) {
            ScheduleRepository.updateSchedule(schedule)
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<Any>) {
        this.list = list
        notifyDataSetChanged()
    }

}