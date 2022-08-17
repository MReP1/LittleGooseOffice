package little.goose.account.ui.schedule

import little.goose.account.logic.ScheduleRepository
import little.goose.account.logic.data.entities.Schedule

object ScheduleHelper {
    var scheduleList: List<Schedule> = emptyList()

    suspend fun initSchedule() {
        scheduleList = ScheduleRepository.getAllSchedule()
    }

}