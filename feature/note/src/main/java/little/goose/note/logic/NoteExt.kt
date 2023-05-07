package little.goose.note.logic

import little.goose.note.data.entities.Note
import little.goose.note.data.entities.NoteContentBlock

val Map<Note, List<NoteContentBlock>>.note: Note get() = keys.first()
val Map<Note, List<NoteContentBlock>>.notes: List<Note> get() = keys.toList()
val Map<Note, List<NoteContentBlock>>.content get() = values.first()
val Map<Note, List<NoteContentBlock>>.contents get() = values.toList()