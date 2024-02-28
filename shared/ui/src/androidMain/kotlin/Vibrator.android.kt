import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf

actual class Vibrator(private val context: Context) {

    private val vibratorService = context.getSystemService(Vibrator::class.java)

    @SuppressLint("MissingPermission")
    actual fun vibrate(effect: Vibration) {
        if (context.checkSelfPermission(Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        when (effect) {
            Vibration.ClickShot -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibratorService?.vibrate(
                        VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                    )
                } else {
                    vibratorService?.vibrate(VibrationEffect.createOneShot(16, 180))
                }
            }

            is Vibration.OneShot -> {
                vibratorService?.vibrate(
                    VibrationEffect.createOneShot(
                        effect.millis,
                        effect.amplitude
                    )
                )
            }
        }
    }

}

actual fun Module.vibrator() {
    singleOf(::Vibrator)
}