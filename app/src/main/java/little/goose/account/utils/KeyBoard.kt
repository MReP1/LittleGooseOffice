package little.goose.account.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import little.goose.account.appContext

object KeyBoard {
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