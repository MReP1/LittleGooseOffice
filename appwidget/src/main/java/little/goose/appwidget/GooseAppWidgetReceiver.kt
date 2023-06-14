package little.goose.appwidget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class GooseAppWidgetReceiver : GlanceAppWidgetReceiver() {
    private val widget get() = GooseAppWidget()

    override val glanceAppWidget: GlanceAppWidget = widget

}