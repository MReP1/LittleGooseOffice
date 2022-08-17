package little.goose.account.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import little.goose.account.R
import little.goose.account.utils.ELog

open class BaseActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ELog.debugLifeCycle("${this.localClassName} - onCreate")

        //设置导航栏颜色
        window.navigationBarColor = ContextCompat.getColor(this, R.color.primary_color)
    }

    override fun onStart() {
        super.onStart()
        ELog.debugLifeCycle("${this.localClassName} - onStart")
    }

    override fun onResume() {
        super.onResume()
        ELog.debugLifeCycle("${this.localClassName} - onResume")
    }

    override fun onPause() {
        super.onPause()
        ELog.debugLifeCycle("${this.localClassName} - onPause")
    }

    override fun onStop() {
        super.onStop()
        ELog.debugLifeCycle("${this.localClassName} - onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        ELog.debugLifeCycle("${this.localClassName} - onDestroy")
    }
}