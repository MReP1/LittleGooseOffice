package little.goose.design.system.util

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource

sealed class Icon {

    data class Drawable(@DrawableRes internal val resId: Int) : Icon()

    data class Vector(internal val icon: ImageVector) : Icon()
}

@Composable
fun Icon.Display(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current
) {
    when (this) {
        is Icon.Drawable -> {
            Icon(
                modifier = modifier,
                painter = painterResource(id = this.resId),
                contentDescription = contentDescription,
                tint = tint
            )
        }

        is Icon.Vector -> {
            Icon(
                modifier = modifier,
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint
            )
        }
    }
}
