package little.goose.account.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import little.goose.account.R
import little.goose.account.utils.debugLifeCycle

open class BaseActivity : AppCompatActivity() {

    init {
        lifecycle.debugLifeCycle()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置导航栏颜色
        window.navigationBarColor = ContextCompat.getColor(this, R.color.primary_color)
    }
}