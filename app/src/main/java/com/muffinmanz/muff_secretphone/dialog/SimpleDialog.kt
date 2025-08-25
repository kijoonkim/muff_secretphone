package com.muffinmanz.muff_secretphone.dialog

import android.content.Context
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import com.muffinmanz.muff_secretphone.R
import com.muffinmanz.muff_secretphone.databinding.DialogSimpleBinding

class SimpleDialog(context: Context, private val message: String) : AlertDialog(context, R.style.AlertDialogLightX) {

  private lateinit var binding: DialogSimpleBinding

  private val modelInAnim: Animation get() = AnimationUtils.loadAnimation(context, R.anim.modal_in)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DialogSimpleBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.apply {
      root.startAnimation(modelInAnim)

      tvMessage.text = message

      btnConfirm.setOnClickListener {
        dismiss()
      }
    }
  }

  override fun show() {
    super.show()
    showCheck = true
  }

  override fun dismiss() {
    super.dismiss()
    showCheck = false
  }

  override fun onStop() {
    super.onStop()
    showCheck = false
  }

  companion object {
    var showCheck = false
  }
}