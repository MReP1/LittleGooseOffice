package little.goose.account.utils

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.getItemCount() = layoutManager?.itemCount ?: 0
fun RecyclerView.getSpanCount() = checkGridLayoutManager()?.spanCount ?: 0

fun RecyclerView.checkGridLayoutManager(): GridLayoutManager? {
    val layoutManager = layoutManager ?: return null
    if (layoutManager !is GridLayoutManager) {
        throw IllegalStateException("Make sure you are using the GridLayoutManager！")
    }
    return layoutManager
}