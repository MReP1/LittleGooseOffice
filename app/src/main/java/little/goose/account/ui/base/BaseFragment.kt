package little.goose.account.ui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import little.goose.account.utils.ELog

open class BaseFragment : Fragment {

    constructor() : super()
    constructor(@LayoutRes layoutId: Int) : super(layoutId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ELog.debugLifeCycle("${this::class.java.simpleName} - onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ELog.debugLifeCycle("${this::class.java.simpleName} - onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        ELog.debugLifeCycle("${this::class.java.simpleName} - onStart")
    }

    override fun onResume() {
        super.onResume()
        ELog.debugLifeCycle("${this::class.java.simpleName} - onResume")
    }

    override fun onPause() {
        super.onPause()
        ELog.debugLifeCycle("${this::class.java.simpleName} - onPause")
    }

    override fun onStop() {
        super.onStop()
        ELog.debugLifeCycle("${this::class.java.simpleName} - onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        ELog.debugLifeCycle("${this::class.java.simpleName} - onDestroy")
    }
}