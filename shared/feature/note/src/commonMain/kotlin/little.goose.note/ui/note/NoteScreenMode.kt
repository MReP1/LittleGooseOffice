package little.goose.note.ui.note

import little.goose.shared.common.CommonParcelable
import little.goose.shared.common.CommonParcelize

@CommonParcelize
enum class NoteScreenMode: CommonParcelable {
    Preview, Edit
}