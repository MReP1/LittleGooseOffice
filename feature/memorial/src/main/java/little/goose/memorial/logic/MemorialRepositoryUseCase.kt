package little.goose.memorial.logic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import little.goose.memorial.data.entities.Memorial

class InsertMemorialUseCase(
    private val memorialRepository: MemorialRepository
) {
    suspend operator fun invoke(memorial: Memorial): Long {
        return memorialRepository.insertMemorial(memorial)
    }
}

class InsertMemorialsUseCase(
    private val memorialRepository: MemorialRepository
) {
    suspend operator fun invoke(memorials: List<Memorial>) {
        return memorialRepository.insertMemorials(memorials)
    }
}

class UpdateMemorialUseCase(
    private val memorialRepository: MemorialRepository
) {
    suspend operator fun invoke(memorial: Memorial) {
        return memorialRepository.updateMemorial(memorial)
    }
}

class UpdateMemorialsUseCase(
    private val memorialRepository: MemorialRepository
) {
    suspend operator fun invoke(memorials: List<Memorial>) {
        return memorialRepository.updateMemorials(memorials)
    }
}

class DeleteMemorialsUseCase(
    private val memorialRepository: MemorialRepository
) {
    private val _deleteMemorialsEvent = MutableSharedFlow<List<Memorial>>()
    val deleteMemorialsEvent = _deleteMemorialsEvent.asSharedFlow()

    suspend operator fun invoke(memorials: List<Memorial>) {
        if (memorials.size == 1) {
            memorialRepository.deleteMemorial(memorials[0])
        } else {
            memorialRepository.deleteMemorials(memorials)
        }
        _deleteMemorialsEvent.emit(memorials)
    }
}

class GetAllMemorialFlowUseCase(
    private val memorialRepository: MemorialRepository
) {
    operator fun invoke(): Flow<List<Memorial>> {
        return memorialRepository.getAllMemorialFlow()
    }
}

class SearchMemorialByTextFlowUseCase(
    private val memorialRepository: MemorialRepository
) {
    operator fun invoke(keyword: String): Flow<List<Memorial>> {
        return memorialRepository.searchMemorialByTextFlow(keyword)
    }
}

class GetMemorialFlowUseCase(
    private val memorialRepository: MemorialRepository
) {
    operator fun invoke(id: Long): Flow<Memorial> {
        return memorialRepository.getMemorialFlow(id)
    }
}

class GetMemorialAtTopFlowUseCase(
    private val memorialRepository: MemorialRepository
) {
    operator fun invoke(): Flow<List<Memorial>> {
        return memorialRepository.getMemorialAtTopFlow()
    }
}

class GetMemorialsByYearMonthFlowUseCase(
    private val memorialRepository: MemorialRepository
) {
    operator fun invoke(year: Int, month: Int): Flow<List<Memorial>> {
        return memorialRepository.getMemorialsByYearMonthFlow(year, month)
    }
}