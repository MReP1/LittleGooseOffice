package little.goose.account.ui.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import little.goose.account.utils.*

class ItemGridLayoutDecoration(
    private val horizonMargin: Int,
    private val verticalMargin: Int,
    midMargin: Int
) : RecyclerView.ItemDecoration() {

    private var itemCount = -1
    private var spanCount = -1
    private val topItemNumber = 1
    private var bottomItemNumber = -1
    private var middleMargin = midMargin/2

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (itemCount == -1) {
            itemCount = parent.getItemCount()
        }
        if (spanCount == -1) {
            spanCount = parent.getSpanCount()
        }
        if (bottomItemNumber == -1) {
            bottomItemNumber = (itemCount + spanCount - 1) / spanCount
        }

        val position = parent.getChildViewHolder(view).bindingAdapterPosition + 1
        val horizontalPosition = position % spanCount

        if (topItemNumber == bottomItemNumber) {
            //如果只有一行
            //1为左边，0为右边，else为中间
            when (horizontalPosition) {
                1 -> outRect.set(horizonMargin, verticalMargin, middleMargin, verticalMargin)
                0 -> outRect.set(middleMargin, verticalMargin, horizonMargin, verticalMargin)
                else -> outRect.set(middleMargin, verticalMargin, middleMargin, verticalMargin)
            }
            return
        }

        when ((position + spanCount - 1) / spanCount) {
            topItemNumber -> {
                when (horizontalPosition) {
                    1 -> outRect.set(horizonMargin, verticalMargin, middleMargin, middleMargin)
                    0 -> outRect.set(middleMargin, verticalMargin, horizonMargin, middleMargin)
                    else -> outRect.set(middleMargin, verticalMargin, middleMargin, middleMargin)
                }
            }
            bottomItemNumber -> {
                when (horizontalPosition) {
                    1 -> outRect.set(horizonMargin, middleMargin, middleMargin, verticalMargin)
                    0 -> outRect.set(middleMargin, middleMargin, horizonMargin, verticalMargin)
                    else -> outRect.set(middleMargin, middleMargin, middleMargin, verticalMargin)
                }
            }
            else -> {
                when (horizontalPosition) {
                    1 -> outRect.set(horizonMargin, middleMargin, middleMargin, middleMargin)
                    0 -> outRect.set(middleMargin, middleMargin, horizonMargin, middleMargin)
                    else -> outRect.set(middleMargin, middleMargin, middleMargin, middleMargin)
                }
            }
        }
    }
}