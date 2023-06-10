package little.goose.schedule.logic

import kotlinx.coroutines.flow.Flow
import little.goose.schedule.data.entities.Schedule

class InsertScheduleUseCase(
    private val scheduleRepository: ScheduleRepository
) {
    suspend operator fun invoke(schedule: Schedule) {
        return scheduleRepository.insertSchedule(schedule)
    }
}

class InsertSchedulesUseCase(
    private val scheduleRepository: ScheduleRepository
) {
    suspend operator fun invoke(schedules: List<Schedule>) {
        return scheduleRepository.insertSchedules(schedules)
    }
}

class UpdateScheduleUseCase(
    private val scheduleRepository: ScheduleRepository
) {
    suspend operator fun invoke(schedule: Schedule) {
        return scheduleRepository.updateSchedule(schedule)
    }
}

class DeleteScheduleUseCase(
    private val scheduleRepository: ScheduleRepository
) {
    suspend operator fun invoke(schedule: Schedule) {
        return scheduleRepository.deleteSchedule(schedule)
    }
}

class DeleteSchedulesUseCase(
    private val scheduleRepository: ScheduleRepository
) {
    suspend operator fun invoke(schedules: List<Schedule>) {
        return scheduleRepository.deleteSchedules(schedules)
    }
}

class GetScheduleByIdFlowUseCase(
    private val scheduleRepository: ScheduleRepository
) {
    operator fun invoke(id: Long): Flow<Schedule> {
        return scheduleRepository.getScheduleByIdFlow(id)
    }
}

class GetAllScheduleFlowUseCase(
    private val scheduleRepository: ScheduleRepository
) {
    operator fun invoke(): Flow<List<Schedule>> {
        return scheduleRepository.getAllScheduleFlow()
    }
}

class GetScheduleByYearMonthFlowUseCase(
    private val scheduleRepository: ScheduleRepository
) {
    operator fun invoke(year: Int, month: Int): Flow<List<Schedule>> {
        return scheduleRepository.getScheduleByYearMonthFlow(year, month)
    }
}

class SearchScheduleByTextFlowUseCase(
    private val scheduleRepository: ScheduleRepository
) {
    operator fun invoke(keyWord: String): Flow<List<Schedule>> {
        return scheduleRepository.searchScheduleByTextFlow(keyWord)
    }
}