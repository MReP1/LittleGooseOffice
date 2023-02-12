package little.goose.account.ui.schedule

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import little.goose.common.ItemSelectCallback
import little.goose.common.MultipleChoseHandler
import little.goose.account.databinding.ItemScheduleBinding
import little.goose.account.logic.data.entities.Schedule

class ScheduleRcvAdapter(
    private var list: List<Schedule>,
    private val callback: ItemSelectCallback<Schedule>? = null,
    private val multipleChoseHandler: MultipleChoseHandler<Schedule>? = null
) : RecyclerView.Adapter<ScheduleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemScheduleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ScheduleViewHolder(binding, multipleChoseHandler)
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