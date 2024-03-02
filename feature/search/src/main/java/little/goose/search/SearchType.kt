package little.goose.search

import androidx.compose.runtime.saveable.Saver

enum class SearchType(val value: Int) {
    Transaction(0), Note(1), Memorial(2);

    companion object {
        const val KEY_SEARCH_TYPE = "search_type"
        fun fromValue(value: Int): SearchType {
            return when (value) {
                0 -> Transaction
                1 -> Note
                2 -> Memorial
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }

        val saver = Saver<SearchType, Any>(
            save = { it.value },
            restore = { fromValue(it as Int) }
        )
    }
}