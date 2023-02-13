package little.goose.office.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import little.goose.office.appScope
import little.goose.common.utils.getRealTime
import little.goose.common.utils.toSignString
import little.goose.office.databinding.ItemHomeWidgetBinding
import little.goose.schedule.data.entities.Schedule

class HomeWidgetRcvAdapter(
    private var list: List<Any>,
    private var fragmentManager: FragmentManager,
    private val updateSchedule: (Schedule) -> Unit
) : RecyclerView.Adapter<HomeWidgetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeWidgetViewHolder {
        val binding = ItemHomeWidgetBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HomeWidgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeWidgetViewHolder, position: Int) {
        when (val item = list[position]) {
            is little.goose.account.data.entities.Transaction -> {
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
            little.goose.common.R.drawable.icon_checked_box
        } else {
            little.goose.common.R.drawable.icon_uncheck_box
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
                    little.goose.common.R.drawable.icon_checked_box
                } else {
                    little.goose.common.R.drawable.icon_uncheck_box
                }
                holder.updateImgResource(tempDrawable)
            }
        }
        holder.itemView.setOnClickListener {
            little.goose.schedule.ui.ScheduleDialogFragment.newInstance(schedule).showNow(fragmentManager, "schedule")
        }
    }

    private fun setTransactionHolder(holder: HomeWidgetViewHolder, transaction: little.goose.account.data.entities.Transaction) {
        holder.setData(
            ItemHomeWidget(
                transaction.content, transaction.money.toSignString(),
                TransactionIconHelper.getIconPath(transaction.icon_id)
            )
        )
        holder.itemView.setOnClickListener {
            little.goose.account.ui.transaction.TransactionDialogFragment.showNow(transaction, fragmentManager)
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<Any>) {
        this.list = list
        notifyDataSetChanged()
    }

}