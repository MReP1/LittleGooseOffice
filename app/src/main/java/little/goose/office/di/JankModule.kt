package little.goose.office.di

import android.util.Log
import androidx.metrics.performance.JankStats
import little.goose.office.MainActivity
import org.koin.dsl.module

val jankStatsModule = module {

    single {
        JankStats.OnFrameListener { frameData ->
            if (frameData.isJank) {
                Log.d("Goose Jank", frameData.toString())
            }
        }
    }

    scope<MainActivity> {
        scoped {
            JankStats.createAndTrack(
                getSource<MainActivity>()!!.window, get()
            )
        }
    }

}