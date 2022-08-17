@file:Suppress("NOTHING_TO_INLINE")

package little.goose.account.utils

import android.view.View

inline fun View.setVisible() {
    if (this.visibility != View.VISIBLE) { this.visibility = View.VISIBLE }
}

inline fun View.setGone() {
    if (this.visibility != View.GONE) { this.visibility = View.GONE }
}