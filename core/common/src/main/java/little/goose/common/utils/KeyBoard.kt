package little.goose.common.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyBoard {

    lateinit var appContext: Context

    fun show(focusView: View){
        val inputManager = appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(focusView, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hide(focusView: View) {
        val inputManager = appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isActive) {
            inputManager.hideSoftInputFromWindow(focusView.applicationWindowToken, 0)
        }
    }
}