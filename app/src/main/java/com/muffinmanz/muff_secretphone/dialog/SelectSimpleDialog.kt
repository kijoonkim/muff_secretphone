package com.muffinmanz.muff_secretphone.dialo

import android.content.Context
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import com.muffinmanz.muff_secretphone.R
import com.muffinmanz.muff_secretphone.databinding.DialogSelectSimpleBinding

class SelectSimpleDialog(context: Context, private val message: String) : AlertDialog(context, R.style.AlertDialogLightX) {
    private lateinit var binding: DialogSelectSimpleBinding

    private val modelInAnim: Animation get() = AnimationUtils.loadAnimation(context, R.anim.modal_in)

    private var onSaveClickListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogSelectSimpleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            root.startAnimation(modelInAnim)

            tvMessage.text = message

            btnSettingSave.setOnClickListener {
                onSaveClickListener?.invoke() // 클릭 리스너 호출
                dismiss()
            }

            btnSettingCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    fun setOnSaveClickListener(listener: () -> Unit) {
        onSaveClickListener = listener
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
