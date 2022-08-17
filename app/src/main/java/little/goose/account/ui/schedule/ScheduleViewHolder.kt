package little.goose.account.ui.schedule

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import little.goose.account.AccountApplication
import little.goose.account.common.MultipleChoseHandler
import little.goose.account.databinding.ItemScheduleBinding
import little.goose.account.logic.ScheduleRepository
import little.goose.account.logic.data.entities.Schedule
import little.goose.account.utils.DateTimeUtils
import little.goose.account.utils.getDate
import little.goose.account.utils.getMonth
import java.util.*

class ScheduleViewHolder(
    private val binding: ItemScheduleBinding,
    private val multipleChoseHandler: MultipleChoseHandler<Schedule>?
) : RecyclerView.ViewHolder(binding.root) {

    fun bindData(schedule: Schedule) {
        binding.apply {
            tvScheduleContent.text = schedule.title
            updateCheckBox(schedule.isfinish)
            initSelect(schedule)
            cbScheduleFinish.setOnClickListener {
                AccountApplication.supervisorScope.launch(Dispatchers.Main) {
                    schedule.isfinish = !schedule.isfinish
                    cbScheduleFinish.isChecked = schedule.isfinish
                    cbScheduleFinish.isClickable = false
                    coroutineScope {
                        updateCheckBox(schedule.isfinish)
                        updateSchedule(schedule)
                    }
                    cbScheduleFinish.isClickable = true
                }
            }
            flScheduleFinish.setOnClickListener {
                AccountApplication.supervisorScope.launch(Dispatchers.Main) {
                    schedule.isfinish = !schedule.isfinish
                    cbScheduleFinish.isChecked = schedule.isfinish
                    flScheduleFinish.isClickable = false
                    coroutineScope {
                        updateCheckBox(schedule.isfinish)
                        updateSchedule(schedule)
                    }
                    flScheduleFinish.isClickable = true
                }
            }
        }

        Calendar.getInstance().apply {
            time = schedule.time
            val hour = DateTimeUtils.getTimeFormatTen(get(Calendar.HOUR_OF_DAY))
            val minute = DateTimeUtils.getTimeFormatTen(get(Calendar.MINUTE))
            val timeText = "$hour : $minute"
            binding.tvScheduleTime.text = timeText
            val dateTime = "${getMonth()}月${getDate()}日"
            binding.tvScheduleDate.text = dateTime
            binding.tvScheduleWeek.text = DateTimeUtils.getWeekDay(get(Calendar.DAY_OF_WEEK))
        }
    }

    private fun updateCheckBox(finish: Boolean) {
        binding.cbScheduleFinish.isChecked = finish
    }

    //多选
    private fun initSelect(schedule: Schedule) {
        //fixme 需要优化
        multipleChoseHandler?.itemList?.contains(schedule)?.let { setSelect(it) }
    }

    fun setSelect(flag: Boolean) {
        if (flag) {
            binding.ivBgVector.visibility = View.VISIBLE
        } else {
            binding.ivBgVector.visibility = View.GONE
        }
    }

    private suspend fun updateSchedule(schedule: Schedule) {
        ScheduleRepository.updateSchedule(schedule)
    }

}