package little.goose.home.data

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Notify：已经定义的不要改动，要加东西就往后面加，COUNT为所有定义的总和
 * */
const val HOME = 0
const val NOTEBOOK = 1
const val ACCOUNT = 2
const val SCHEDULE = 3
const val MEMORIAL = 4
const val COUNT = 5

enum class HomePage(
    val index: Int,
    val icon: ImageVector,
    @StringRes val labelRes: Int
) {
    Home(
        index = 0,
        icon = Icons.Rounded.Home,
        labelRes = little.goose.home.R.string.home
    ),
    Notebook(
        index = 1,
        icon = Icons.Rounded.EditNote,
        labelRes = little.goose.note.R.string.notebook
    ),
    ACCOUNT(
        index = 2,
        icon = Icons.Rounded.AccountBalance,
        labelRes = little.goose.account.R.string.account
    ),
    Schedule(
        index = 3,
        icon = Icons.Rounded.Schedule,
        labelRes = little.goose.schedule.R.string.schedule
    ),
    Memorial(
        index = 4,
        icon = Icons.Rounded.Memory,
        labelRes = little.goose.memorial.R.string.memorial
    );

    companion object {
        fun fromPageIndex(index: Int): HomePage {
            return when (index) {
                0 -> Home
                1 -> Notebook
                2 -> ACCOUNT
                3 -> Schedule
                4 -> Memorial
                else -> throw Exception("No such page index.")
            }
        }
    }
}