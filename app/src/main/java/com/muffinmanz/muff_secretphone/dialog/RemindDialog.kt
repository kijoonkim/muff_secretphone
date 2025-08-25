package com.muffinmanz.muff_secretphone.dialog

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.muffinmanz.muff_secretphone.R
import com.muffinmanz.muff_secretphone.data.db.VideoRecDataDao_Impl
import com.muffinmanz.muff_secretphone.data.model.VideoListModel
import com.muffinmanz.muff_secretphone.databinding.DialogRemindBinding
import com.muffinmanz.muff_secretphone.ui.videoresult.VideoResultViewModel
import com.muffinmanz.muff_secretphone.utilities.CommonUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class RemindDialog @Inject constructor(context: Context, private val model: VideoListModel, private val confirmClick: (AlertDialog) -> Unit, private val confirmEnglishClick: (AlertDialog) -> Unit) : AlertDialog(context, R.style.AlertDialogLightX),
  CoroutineScope {

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + Job()

  private lateinit var binding: DialogRemindBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DialogRemindBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.apply {
      interViewer = model

      CommonUtils.bindImageFromString(ivUser, model.img)
      CommonUtils.bindGenderCheck(tvGenderInfo, model.sex)

      btnClose.setOnClickListener {
        dismiss()
      }

      btnStartShooting.setOnClickListener {
        confirmClick(this@RemindDialog)
      }

      launch {
        if (model.hasKoreanVideo == true) {
          btnStartShooting.isEnabled = false
        }
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