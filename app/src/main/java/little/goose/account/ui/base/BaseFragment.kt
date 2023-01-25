package little.goose.account.ui.base

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import little.goose.account.utils.debugLifeCycle

open class BaseFragment : Fragment {

    constructor() : super()
    constructor(@LayoutRes layoutId: Int) : super(layoutId)

    init {
        lifecycle.debugLifeCycle()
    }
}