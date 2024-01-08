package little.goose.design.system.util

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "Phone", device = Devices.PHONE)
@Preview(name = "Phone - Landscape", device = "spec:width=411dp,height=891dp,orientation=landscape")
@Preview(name = "Unfolded Foldable", device = Devices.FOLDABLE)
@Preview(name = "Tablet", device = Devices.TABLET)
@Preview(name = "Desktop", device = Devices.DESKTOP)
annotation class PreviewMultipleScreenSizes