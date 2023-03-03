package little.goose.common.collections

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class CircularLinkList<T> {

    var size = 0
        private set

    private var headNote: Note<T>? = null
    private var tailNote: Note<T>? = null

    private val _currentNote: MutableStateFlow<Note<T>?> = MutableStateFlow(null)
    val currentNote = _currentNote.map { it?.element }

    fun add(element: T) {
        val note = Note(element)
        val headNote = headNote
        val tailNote = tailNote
        if (headNote == null || tailNote == null) {
            this.headNote = note
            this.tailNote = note
            _currentNote.value = note
        } else {
            tailNote._next = note
            note._next = headNote
            this.tailNote = note
        }
        size++
    }

    fun remove(element: T) {
        var note: Note<T>? = headNote
        if (note == element) {
            headNote?._next = null
            headNote = null
        } else {
            while (note != tailNote || note != null) {
                if (note?._next == element) {
                    note?._next = element?._next
                    if (element == tailNote) {
                        tailNote = note
                    }
                    element?._next = null
                    break
                }
                note = note?._next
            }
        }
        if (note?._next == note) {
            note?._next = null
        }
        size--
    }

    fun next(): T {
        val note = _currentNote.value?.next
        _currentNote.value = note
        return note!!.element
    }

    class Note<T>(
        val element: T,
        internal var _next: Note<T>? = null,
    ) {
        val next get() = _next ?: this
    }
}