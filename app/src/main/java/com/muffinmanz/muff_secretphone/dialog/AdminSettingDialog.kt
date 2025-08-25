package com.muffinmanz.muff_secretphone.dialog

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.muffinmanz.muff_secretphone.R
import com.muffinmanz.muff_secretphone.data.prefs.AppPreferencesHelper
import com.muffinmanz.muff_secretphone.databinding.DialogAdminSettingBinding
import com.muffinmanz.muff_secretphone.dialo.SelectSimpleDialog

class AdminSettingDialog(
    context: Context,
    private val apkSync: (AlertDialog) -> Unit,
    private val onSettingsBack: () -> Unit
) : AlertDialog(context, R.style.AlertDialogLightX) {

    private lateinit var binding: DialogAdminSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAdminSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnContentSettings.setOnClickListener {
            dismiss()
            onSettingsBack()
        }

        binding.btnContentClose.setOnClickListener {
            dismiss()
        }

        binding.btnSettingCancel.setOnClickListener {
            dismiss()
        }

        binding.apkFileDwnl.setOnClickListener {
            val selectSimpleDialog = SelectSimpleDialog(context, "APK 설치파일을 다운로드 하시겠습니까?")
            selectSimpleDialog.show()

            selectSimpleDialog.setOnSaveClickListener {
                apkSync(this@AdminSettingDialog)
            }
        }

        binding.btnSettingSave.setOnClickListener {
            val selectSimpleDialog = SelectSimpleDialog(context, "저장 하시겠습니까?")
            selectSimpleDialog.show()

            selectSimpleDialog.setOnSaveClickListener {
                val sltFpsRadioBtn = binding.rgFps.checkedRadioButtonId
                val sltRsltRadioBtn = binding.rgRslt.checkedRadioButtonId

                if (sltFpsRadioBtn != -1 || sltRsltRadioBtn != -1) {
                    val sharedPref = context.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()

                    editor.putInt("SltFpsRate", sltFpsRadioBtn)
                    editor.putInt("SltRsltRate", sltRsltRadioBtn)

                    val userFps = when (sltFpsRadioBtn) {
                        R.id.elvt_fps -> 30
                        R.id.nrml_fps -> 20
                        R.id.rdcd_fps -> 10
                        else -> 30
                    }

                    val resolutionIndex = when (sltRsltRadioBtn) {
                        R.id.elvt_rslt -> 0
                        R.id.nrml_rslt -> 1
                        R.id.rdcd_rslt -> 2
                        else -> 1
                    }

                    val prefsHelper = AppPreferencesHelper(context, context.getString(R.string.preferences_key))
                    prefsHelper.fps = userFps
                    prefsHelper.resolutionIndex = resolutionIndex

                    editor.apply()

                    val simpleDialog = SimpleDialog(context, "설정 값이 저장되었습니다.")
                    simpleDialog.show()
                }
            }
        }

        val sharedPref = context.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)
        val svFpsRadioBtn = sharedPref.getInt("SltFpsRate", R.id.elvt_fps)
        val svRsltRadioBtn = sharedPref.getInt("SltRsltRate", R.id.nrml_rslt)

        binding.rgFps.check(svFpsRadioBtn)
        binding.rgRslt.check(svRsltRadioBtn)
    }
}

