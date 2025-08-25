package com.muffinmanz.muff_secretphone.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.muffinmanz.muff_secretphone.di.PreferenceInfo
import javax.inject.Inject

class AppPreferencesHelper @Inject constructor(
    context: Context,
    @PreferenceInfo prefFileName: String?
) : PreferencesHelper {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)

    override var ip: String?
        get() = prefs.getString(PREF_KEY_BASE_IP, null)
        set(ip) {
            prefs.edit().putString(PREF_KEY_BASE_IP, ip).apply()
        }

    override var baseUrl: String?
        get() = prefs.getString(PREF_KEY_BASE_URL, null)
        set(baseUrl) {
            prefs.edit().putString(PREF_KEY_BASE_URL, baseUrl).apply()
        }

    override var resolutionIndex: Int?
        get() = prefs.getInt(PREF_KEY_RESOLUTION, 2)
        set(resolutionIndex) {
            prefs.edit().putInt(PREF_KEY_RESOLUTION, resolutionIndex!!).apply()
        }

    override var fps: Int?
        get() = prefs.getInt(PREF_KEY_FPS, 30)
        set(fps) {
            prefs.edit().putInt(PREF_KEY_FPS, fps!!).apply()
        }


    companion object {
        private const val PREF_KEY_BASE_IP = "PK_BASE_IP"
        private const val PREF_KEY_BASE_URL = "PK_BASE_URL"
        private const val PREF_KEY_RESOLUTION = "PREF_KEY_RESOLUTION"
        private const val PREF_KEY_FPS = "PREF_KEY_FPS"
    }
}