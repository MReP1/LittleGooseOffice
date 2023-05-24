package little.goose.search

enum class SearchType(val value: Int) {
    Transaction(0), Note(1), Memorial(2), Schedule(3);

    companion object {
        const val KEY_SEARCH_TYPE = "search_type"
        fun fromValue(value: Int): SearchType {
            return when (value) {
                0 -> Transaction
                1 -> Note
                2 -> Memorial
                3 -> Schedule
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}