package little.goose.appwidget.layout

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min

internal enum class AppWidgetSizeResponsive(internal val size: DpSize) {
    SQUARE_50(DpSize(50.dp, 50.dp)),
    SQUARE_100(DpSize(100.dp, 100.dp)),
    SQUARE_150(DpSize(150.dp, 150.dp)),
    SQUARE_200(DpSize(200.dp, 200.dp)),
    SQUARE_250(DpSize(250.dp, 250.dp)),
    SQUARE_300(DpSize(300.dp, 300.dp)),
    RECTANGLE_100_50(DpSize(100.dp, 50.dp)),
    RECTANGLE_200_100(DpSize(200.dp, 100.dp)),
    RECTANGLE_300_150(DpSize(300.dp, 150.dp)),
    RECTANGLE_400_200(DpSize(400.dp, 200.dp)),
    RECTANGLE_50_100(DpSize(50.dp, 100.dp)),
    RECTANGLE_100_200(DpSize(100.dp, 200.dp)),
    RECTANGLE_150_300(DpSize(150.dp, 300.dp)),
    RECTANGLE_200_400(DpSize(200.dp, 400.dp)),
}

internal val DpSize.minSide get() = min(width, height)
internal val DpSize.maxSide get() = max(width, height)