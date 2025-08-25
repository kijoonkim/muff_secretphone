package com.muffinmanz.muff_secretphone

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.PackageInfoCompat
import androidx.databinding.DataBindingUtil
import com.muffinmanz.muff_secretphone.databinding.ActivityMainBinding
import com.muffinmanz.muff_secretphone.extensions.hide
import com.muffinmanz.muff_secretphone.extensions.observe
import com.muffinmanz.muff_secretphone.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

    loading.observe(this) {
      if(it) binding.frLoadingContainer.show()
      else binding.frLoadingContainer.hide()
    }
  }

  // 바탕 클릭시 키보드 숨기기
  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    val view = this.currentFocus
    if (view != null) {
      val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    return super.dispatchTouchEvent(ev)
  }

  companion object {
    val loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
  }
}