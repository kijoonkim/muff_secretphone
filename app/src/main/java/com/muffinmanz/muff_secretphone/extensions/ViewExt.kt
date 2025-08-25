package com.muffinmanz.muff_secretphone.extensions

import android.app.Service
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager

fun View.hide(gone: Boolean = true) {
  visibility = if (gone) View.GONE else View.INVISIBLE
}

fun View.show() {
  visibility = View.VISIBLE
}

fun View.hideKeyboard() {
  (this.context.getSystemService(Service.INPUT_METHOD_SERVICE) as? InputMethodManager)
    ?.hideSoftInputFromWindow(this.windowToken, 0)
}

// define 'afterMeasured' layout listener:
inline fun <T: View> T.afterMeasured(crossinline f: T.() -> Unit) {
  viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
    override fun onGlobalLayout() {
      if (measuredWidth > 0 && measuredHeight > 0) {
        viewTreeObserver.removeOnGlobalLayoutListener(this)
        f()
      }
    }
  })
}