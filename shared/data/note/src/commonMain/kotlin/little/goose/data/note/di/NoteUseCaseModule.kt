package little.goose.data.note.di

import little.goose.data.note.domain.DeleteBlockUseCase
import little.goose.data.note.domain.DeleteBlockWithNoteIdUseCase
import little.goose.data.note.domain.DeleteNoteAndItsBlocksListUseCase
import little.goose.data.note.domain.DeleteNoteAndItsBlocksUseCase
import little.goose.data.note.domain.DeleteNoteIdListFlowUseCase
import little.goose.data.note.domain.GetNoteFlowUseCase
import little.goose.data.note.domain.GetNoteWithContentByKeywordFlowUseCase
import little.goose.data.note.domain.GetNoteWithContentFlowUseCase
import little.goose.data.note.domain.GetNoteWithContentFlowWithNoteIdUseCase
import little.goose.data.note.domain.InsertOrReplaceNoteContentBlockUseCase
import little.goose.data.note.domain.InsertOrReplaceNoteContentBlocksUseCase
import little.goose.data.note.domain.InsertOrReplaceNoteUseCase
import little.goose.data.note.domain.GetNoteWithContentResultByKeywordFlowUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val noteUseCaseModule = module {

    factoryOf(::DeleteBlockUseCase)

    factoryOf(::DeleteBlockWithNoteIdUseCase)

    factoryOf(::DeleteNoteAndItsBlocksUseCase)

    factoryOf(::GetNoteFlowUseCase)

    factoryOf(::GetNoteWithContentFlowUseCase)

    factoryOf(::GetNoteWithContentFlowWithNoteIdUseCase)

    factoryOf(::InsertOrReplaceNoteContentBlockUseCase)

    factoryOf(::InsertOrReplaceNoteContentBlocksUseCase)

    factoryOf(::InsertOrReplaceNoteUseCase)

    factoryOf(::DeleteNoteAndItsBlocksListUseCase)

    factoryOf(::GetNoteWithContentByKeywordFlowUseCase)

    factoryOf(::GetNoteWithContentResultByKeywordFlowUseCase)

    factoryOf(::DeleteNoteIdListFlowUseCase)

}