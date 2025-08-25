package com.muffinmanz.muff_secretphone.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.muffinmanz.muff_secretphone.R

class CustomLoadingView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {

  private val modelInAnim: Animation get() = AnimationUtils.loadAnimation(context, R.anim.modal_in)

  init {
    Glide.with(this).load(R.drawable.loading_ubt).into(this)
  }

  override fun onVisibilityChanged(changedView: View, visibility: Int) {
    super.onVisibilityChanged(changedView, visibility)
    if(visibility == View.VISIBLE) {
      (parent as View).animation = modelInAnim
    }
  }
}