package little.goose.office

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import little.goose.common.utils.getDataOrDefault
import little.goose.design.system.theme.AccountTheme
import little.goose.home.data.HOME
import little.goose.home.utils.KEY_PREF_PAGER
import little.goose.home.utils.homeDataStore
import little.goose.ui.screen.LoadingScreen

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @OptIn(ExperimentalAnimationGraphicsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isAppInit }
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            val initPage = homeDataStore.getDataOrDefault(KEY_PREF_PAGER, HOME)
            setContent {
                AccountTheme {
                    LoadingScreen(
                        modifier = Modifier
                            .fillMaxSize(),
                        content = {
                            Column {
                                val context = LocalContext.current
                                val image: AnimatedVectorDrawableCompat = remember {
                                    AnimatedVectorDrawableCompat.create(
                                        context,
                                        R.drawable.anim_little_goose
                                    )!!
                                }
                                var atEnd by remember { mutableStateOf(false) }

                                LaunchedEffect(key1 = atEnd, block = {
                                    if (atEnd) {
                                        image.stop()
                                    } else {
                                        image.start()
                                    }
                                })

                                AndroidView(
                                    factory = {
                                        ImageView(it)
                                    }, modifier = Modifier
                                        .size(200.dp)
                                        .clickable {
                                            atEnd = !atEnd
                                        }) {
                                    it.setImageDrawable(image)
                                }

                                val imageVector =
                                    AnimatedImageVector.animatedVectorResource(id = R.drawable.anim_little_goose)

                                Image(
                                    painter = rememberAnimatedVectorPainter(imageVector, atEnd),
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clickable {
                                            atEnd = !atEnd
                                        },
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            }
        }
    }

}