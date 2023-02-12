package little.goose.schedule.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import little.goose.common.ItemSelectCallback
import little.goose.common.MultipleChoseHandler
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.databinding.ItemScheduleBinding

class ScheduleRcvAdapter(
    private var list: List<Schedule>,
    private val callback: ItemSelectCallback<Schedule>? = null,
    private val multipleChoseHandler: MultipleChoseHandler<Schedule>? = null,
    private val updateSchedule: (Schedule) -> Unit
) : RecyclerView.Adapter<ScheduleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemScheduleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ScheduleViewHolder(binding, multipleChoseHandler, updateSchedule)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = list[position]
        holder.apply {
            bindData(schedule)
            itemView.setOnClickListener {
                if (multipleChoseHandler?.isMultipleChose?.value == true) {
                    multiClick(holder, schedule)
                } else {
                    callback?.onItemClick(schedule)
                }
            }
            itemView.setOnLongClickListener {
                if (multipleChoseHandler?.isMultipleChose?.value == false) {
                    multipleChoseHandler.ready()
                    multiClick(holder, schedule)
                }
                return@setOnLongClickListener true
            }
        }
    }

    private fun multiClick(holder: ScheduleViewHolder, schedule: Schedule) {
        multipleChoseHandler?.let { handler ->
            val flag = handler.clickItem(schedule)
            holder.setSelect(flag)
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<Schedule>) {
        this.list = list
        notifyDataSetChanged()
    }
}