import org.koin.core.module.Module

actual class Vibrator {

    actual fun vibrate(effect: Vibration) {
    }

}

actual fun Module.vibrator() {
    single { Vibrator() }
}