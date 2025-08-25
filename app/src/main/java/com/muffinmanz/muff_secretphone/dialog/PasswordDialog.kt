package com.muffinmanz.muff_secretphone.dialog

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import com.muffinmanz.muff_secretphone.R
import com.muffinmanz.muff_secretphone.databinding.DialogPasswordBinding


class PasswordDialog(context: Context, private val confirm: (AlertDialog) -> Unit) : AlertDialog(context, R.style.AlertDialogLightX) {

  private lateinit var binding: DialogPasswordBinding

  private val modelInAnim: Animation get() = AnimationUtils.loadAnimation(context, R.anim.modal_in)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DialogPasswordBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.apply {
      root.startAnimation(modelInAnim)

      btnClose.setOnClickListener { dismiss() }
      btnConfirm.setOnClickListener {
        if (etInputAdmin.text.toString() == "0318") {
          confirm(this@PasswordDialog)
        } else {
          SimpleDialog(context, "패스워드가 맞지 않습니다.").show()
        }
      }
    }
  }

  override fun onStart() {
    super.onStart()
    window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    binding.etInputAdmin.requestFocus()
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