package little.goose.home.data

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Notify：已经定义的不要改动，要加东西就往后面加
 * */
const val HOME = 0
const val NOTEBOOK = 1
const val ACCOUNT = 2
const val MEMORIAL = 3

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
    Account(
        index = 2,
        icon = Icons.Rounded.Savings,
        labelRes = little.goose.account.R.string.account
    ),
    Memorial(
        index = 3,
        icon = Icons.Rounded.Event,
        labelRes = little.goose.memorial.R.string.memorial
    );

    companion object {
        fun fromPageIndex(index: Int): HomePage {
            return when (index) {
                HOME -> Home
                NOTEBOOK -> Notebook
                ACCOUNT -> Account
                MEMORIAL -> Memorial
                else -> throw Exception("No such page index. $index")
            }
        }
    }
}