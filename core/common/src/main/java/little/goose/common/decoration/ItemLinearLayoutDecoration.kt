package little.goose.common.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import little.goose.account.utils.getItemCount

open class ItemLinearLayoutDecoration(
    private val horizonMargin: Int,
    private val verticalMargin: Int,
    private val middleMargin: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getItemCount() == 1) {
            outRect.set(horizonMargin, verticalMargin, horizonMargin, verticalMargin)
            return
        }
        when (parent.getChildViewHolder(view).adapterPosition) {
            parent.adapter?.itemCount?.minus(1) ?: 0 -> {
                outRect.set(horizonMargin, 0, horizonMargin, verticalMargin)
            }
            0 -> {
                outRect.set(horizonMargin, verticalMargin, horizonMargin, middleMargin)
            }
            else -> {
                outRect.set(horizonMargin, 0, horizonMargin, middleMargin)
            }
        }
    }
}