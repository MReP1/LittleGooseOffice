package little.goose.account.utils

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Suppress("unused", "NOTHING_TO_INLINE")
inline fun <V : ViewBinding> Fragment.viewBinding(noinline viewBinder: (View) -> V)
        : ReadOnlyProperty<Fragment, V> = FragmentViewBindingProperty(viewBinder)

class FragmentViewBindingProperty<V : ViewBinding>(private val viewBinder: (View) -> V) :
    ReadOnlyProperty<Fragment, V> {

    private var binding: V? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): V {
        return binding ?: run {
            thisRef.viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    // Fragment.viewLifecycleOwner call LifecycleObserver.onDestroy() before Fragment.onDestroyView().
                    // That's why we need to postpone reset of the viewBinding
                    Handler(Looper.getMainLooper()).post { binding = null }
                    thisRef.viewLifecycleOwner.lifecycle.removeObserver(this)
                }
            })
            val view = thisRef.requireView()
            viewBinder(view).also { binding = it }
        }
    }

}