package com.muffinmanz.muff_secretphone.utilities

import android.graphics.Color
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.muffinmanz.muff_secretphone.R
import com.muffinmanz.muff_secretphone.extensions.getColorRes
import com.muffinmanz.muff_secretphone.extensions.hide
import com.muffinmanz.muff_secretphone.extensions.show

object CommonUtils {
  var defaultHost: String = "http://192.168."
  var defaultPort: String = ":8080"
  var downloadUrl: String = ""
  var uploadUrl: String = ""
  var ip: String = ""
  var ip1: String = ""

  fun bindChangeBackground(view: TextView, state: String?) {
    if (!state.isNullOrEmpty()) {
      when (state) {
        "Y" -> {
          view.setBackgroundColor(view.context.getColorRes(R.color.positive))
          view.setTextColor(Color.WHITE)
        }
        "N" -> {
          view.setBackgroundColor(view.context.getColorRes(R.color.negative))
          view.setTextColor(Color.WHITE)
        }
        else -> {
          view.setBackgroundColor(Color.WHITE)
          view.setTextColor(Color.BLACK)
        }
      }
    } else {
      view.setBackgroundColor(Color.WHITE)
      view.setTextColor(Color.BLACK)
    }
  }

  fun bindCloseState(btn: android.widget.Button, state: String?) {
    if (!state.isNullOrEmpty()) {
      if (state == "Y" || state == "N") {
        btn.setBackgroundResource(R.drawable.ic_close_white)
      } else {
        btn.setBackgroundResource(R.drawable.ic_close_black)
      }
    } else {
      btn.setBackgroundResource(R.drawable.ic_close_black)
    }
  }

  fun bindImageFromString(iv: ImageView, btArr: ByteArray?) {
    if (btArr != null) {
      Glide.with(iv).load(btArr).into(iv)
    }
  }

  fun bindGenderCheck(tv: TextView, sex: String?) {
    if (!sex.isNullOrEmpty()) {
      if (sex.equals("21201", true)) tv.text = "남자"
      else tv.text = "여자"
    }
  }

  fun bindPosVisible(btn: Button, iCheck: String?) {
    if (!iCheck.isNullOrEmpty()) {
      if (iCheck.equals("Y", true)) btn.hide()
      else btn.show()
    } else btn.show()
  }

  fun bindChangeText(view: TextView, state: String?) {
    if (!state.isNullOrEmpty()) {
      when (state) {
        "Y" -> {
          view.setTextColor(view.context.getColorRes(R.color.positive))
          view.text = "응시"
        }
        "N" -> {
          view.setTextColor(view.context.getColorRes(R.color.negative))
          view.text = "결시"
        }
        else -> {
          view.setTextColor(view.context.getColorRes(R.color.black))
          view.text = "-"
        }
      }
    } else {
      view.text = "-"
    }
  }

  fun bindNegVisible(btn: Button, iCheck: String?) {
    if (!iCheck.isNullOrEmpty()) {
      if (iCheck.equals("N", true)) btn.hide()
      else btn.show()
    } else btn.show()
  }

  fun bindVideoCompleteTextChange(tv: TextView, state: String?) {
    if (!state.isNullOrEmpty()) {
      tv.text = state
      when (state) {
        "완료" -> {
          tv.setTextColor(tv.resources.getColor(R.color.positive, null))
        }
        else -> {
          tv.setTextColor(Color.BLACK)
        }
      }
    } else {
      tv.setTextColor(Color.BLACK)
    }
  }
}