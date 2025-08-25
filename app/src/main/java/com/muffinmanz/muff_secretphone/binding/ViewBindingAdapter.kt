package com.muffinmanz.muff_secretphone.binding

import android.view.View

@androidx.databinding.BindingAdapter("isGone")
private fun bindIsGone(view: View, isGone: Boolean) {
  view.visibility = if (isGone) {
    View.VISIBLE
  } else {
    View.GONE
  }
}