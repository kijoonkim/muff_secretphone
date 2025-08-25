package com.muffinmanz.muff_secretphone.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.muffinmanz.muff_secretphone.ui.splash.SplashFragment

class ShortcutReceiver : BroadcastReceiver() {

  val shortcutInstallAction = "android.intent.action.CREATE_SHORTCUT"

  override fun onReceive(context: Context?, intent: Intent) {

    if (shortcutInstallAction == intent.action) {
      SplashFragment.shortCutCompletedLiveData.postValue(true)
    }
  }
}