package little.goose.data.note.domain

import kotlinx.coroutines.flow.Flow
import little.goose.data.note.NoteRepository
import little.goose.data.note.bean.NoteWithContent

class GetNoteWithContentByKeywordFlowUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(keyword: String): Flow<List<NoteWithContent>> {
        return repository.getNoteWithContentFlowByKeyword(keyword)
    }
}