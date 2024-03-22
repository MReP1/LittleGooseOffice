package little.goose.data.note.domain

import LocalDataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import little.goose.data.note.NoteRepository
import little.goose.data.note.bean.NoteWithContent

class GetNoteWithContentByKeywordFlowUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(keyword: String): Flow<List<NoteWithContent>> {
        return repository.getNoteWithContentFlowByKeyword(keyword)
    }
}

class GetNoteWithContentResultByKeywordFlowUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(keyword: String): Flow<LocalDataResult<List<NoteWithContent>>> {
        return flow {
            emit(LocalDataResult.Loading)
            repository.getNoteWithContentFlowByKeyword(keyword).collect {
                emit(LocalDataResult.Data(it))
            }
        }
    }
}