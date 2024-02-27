package little.goose.shared.ui.icon

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Stable
private data class PullToSearchIconCacheCurrentState(var state: Boolean)

@Stable
private data class PullToSearchIconDrawCache(
    val cachePath: Path,
    val cachePathMeasure: PathMeasure,
    val cachePathToDraw: Path,
)

@Composable
fun PullToSearchIcon(
    modifier: Modifier = Modifier,
    progress: Float,
    color: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String?
) {

    val cacheCurrentState = remember { PullToSearchIconCacheCurrentState(false) }

    if (progress == 1F && !cacheCurrentState.state) {
        cacheCurrentState.state = true
    } else if (cacheCurrentState.state && progress == 0F) {
        cacheCurrentState.state = false
    }

    val semantics = if (contentDescription != null) {
        Modifier.semantics {
            this.contentDescription = contentDescription
            this.role = Role.Image
        }
    } else Modifier

    val drawCache = remember {
        PullToSearchIconDrawCache(
            cachePath = Path(),
            cachePathMeasure = PathMeasure(),
            cachePathToDraw = Path()
        )
    }

    Canvas(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .then(semantics)
    ) {
        val arcProgress = if (cacheCurrentState.state) {
            if (progress < 0.22F) 0F else {
                ((progress - 0.22F) / 0.78F).coerceAtMost(1F)
            }
        } else progress

        val lineProgress = if (cacheCurrentState.state) {
            if (progress < 0.16F) 0F else ((progress - 0.16F) / 0.72F).coerceAtMost(1F)
        } else {
            if (progress < 0.12F) 0F else ((progress - 0.12F) / 0.72F).coerceAtMost(1F)
        }

        val arrowProgress = 1F - if (cacheCurrentState.state) {
            (progress / 0.36F).coerceAtMost(1F)
        } else {
            if (progress < 0.12F) 0F else {
                ((progress - 0.12F) / 0.36F).coerceAtMost(1F)
            }
        }

        val minSide = min(size.width, size.height)
        val strokeWidth = minSide / 12
        val pathStyle = Stroke(
            width = strokeWidth,
            join = StrokeJoin.Round,
            cap = StrokeCap.Round
        )
        val cachePath = drawCache.cachePath
        val cachePathMeasure = drawCache.cachePathMeasure
        val cachePathToDraw = drawCache.cachePathToDraw
        val width = size.width
        val height = size.height
        val arcSize = width * 4 / 7
        val radius = arcSize / 2
        val cos45 = 0.7071F

        val lineStartOffset = Offset(
            width / 2 + (width * 0.375F - radius - radius * cos45) * lineProgress,
            (height * 0.1675F - (0.045F * height * lineProgress)) +
                    (radius + radius * cos45) * lineProgress
        )
        val lineEndOffset = Offset(
            width / 2 - lineProgress * width * 0.375F,
            height * 0.875F
        )
        drawLine(
            color = color,
            start = lineStartOffset,
            end = lineEndOffset,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        cachePath.reset()
        cachePath.moveTo(lineEndOffset.x, lineEndOffset.y)
        cachePath.relativeLineTo(-width / 5, -height / 5)
        cachePathMeasure.setPath(cachePath, false)
        cachePathToDraw.reset()
        val bottomLeftResult = cachePathMeasure.getSegment(
            startDistance = 0F,
            stopDistance = cachePathMeasure.length * arrowProgress,
            destination = cachePathToDraw
        )
        if (bottomLeftResult) {
            drawPath(cachePathToDraw, color, 1F, pathStyle)
        }

        cachePath.reset()
        cachePath.moveTo(lineEndOffset.x, lineEndOffset.y)
        cachePath.relativeLineTo(width / 5, -height / 5)
        cachePathMeasure.setPath(cachePath, false)
        cachePathToDraw.reset()
        val bottomRightResult = cachePathMeasure.getSegment(
            startDistance = 0F,
            stopDistance = cachePathMeasure.length * arrowProgress,
            destination = cachePathToDraw
        )
        if (bottomRightResult) {
            drawPath(cachePathToDraw, color, 1F, pathStyle)
        }

        drawArc(
            color = color,
            startAngle = -45F,
            sweepAngle = 360F * arcProgress,
            useCenter = false,
            size = Size(arcSize, arcSize),
            style = pathStyle,
            topLeft = Offset((width / 8 * 7 - arcSize), (height / 8))
        )
    }
}