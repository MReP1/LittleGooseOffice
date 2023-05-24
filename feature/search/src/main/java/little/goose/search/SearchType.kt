package little.goose.search

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
enum class SearchType : Parcelable {
    Transaction, Note, Memorial, Schedule;

    companion object {
        const val KEY_SEARCH_TYPE = "search_type"
    }
}