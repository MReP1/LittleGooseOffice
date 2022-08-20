package little.goose.account.utils

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Suppress("unused")
fun <V : ViewBinding> Fragment.viewBinding(viewBinder: (View) -> V)
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

@Suppress("unused")
fun <V : ViewBinding> Activity.viewBinding(
    viewInflater: (LayoutInflater) -> V
): ReadOnlyProperty<Activity, V> = ActivityViewBindingProperty(viewInflater)

class ActivityViewBindingProperty<V : ViewBinding>(
    private val viewInflater: (LayoutInflater) -> V
) : ReadOnlyProperty<Activity, V> {

    private var binding: V? = null

    override fun getValue(thisRef: Activity, property: KProperty<*>): V {
        return binding ?: viewInflater(thisRef.layoutInflater).also {
            thisRef.setContentView(it.root)
            binding = it
        }
    }

}

@Suppress("unused")
fun <V : ViewBinding> ViewGroup.viewBinding(
    @LayoutRes layoutRes: Int,
    viewBinder: (View) -> V
): ReadOnlyProperty<ViewGroup, V> = ViewViewBindingProperty(layoutRes, viewBinder)

class ViewViewBindingProperty<V : ViewBinding>(
    @LayoutRes private val layoutRes: Int,
    private val viewBinder: (View) -> V
) : ReadOnlyProperty<ViewGroup, V> {

    private var binding: V? = null

    override fun getValue(thisRef: ViewGroup, property: KProperty<*>): V {
        return binding ?: viewBinder(View.inflate(thisRef.context, layoutRes, thisRef)).also {
            binding = it
        }
    }

}