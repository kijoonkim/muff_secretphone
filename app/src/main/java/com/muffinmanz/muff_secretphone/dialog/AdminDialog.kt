package com.muffinmanz.muff_secretphone.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.muffinmanz.muff_secretphone.R
import com.muffinmanz.muff_secretphone.databinding.DialogAdminBinding
import com.muffinmanz.muff_secretphone.ui.login.LoginViewModel
import com.muffinmanz.muff_secretphone.utilities.CommonUtils

class AdminDialog(
    context: Context,
    private val loginViewModel: LoginViewModel,
    private val hqSync: (AlertDialog) -> Unit,
    private val resultSync: (AlertDialog) -> Unit,
    private val apkSync: (AlertDialog) -> Unit,
    private val videoSync: (AlertDialog) -> Unit,
    private val deleteHistory: (AlertDialog) -> Unit,
    private val dataUpdate: (AlertDialog) -> Unit
) : AlertDialog(context, R.style.AlertDialogLightX) {
    private lateinit var binding: DialogAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            loginViewModel.updateLogInfo()
            tvHqSyncLog.text = loginViewModel.updateLog.value
            tvResultSyncLog.text = loginViewModel.uploadLog.value

            if (CommonUtils.ip.isNotEmpty()) {
                etIpInfo.setText(CommonUtils.ip)
            }
            if(CommonUtils.ip1.isNotEmpty()) {
                etIpInfo1.setText(CommonUtils.ip1)
            } else {
                etIpInfo1.setText("0")
                CommonUtils.ip1 = "0"
            }

            etIpInfo.addTextChangedListener {
                CommonUtils.ip = it.toString()
                CommonUtils.uploadUrl = CommonUtils.defaultHost + CommonUtils.ip1 + "." + it.toString() + CommonUtils.defaultPort
                CommonUtils.downloadUrl = CommonUtils.defaultHost + CommonUtils.ip1 + "." + it.toString() + CommonUtils.defaultPort
            }

            etIpInfo1.addTextChangedListener {
                CommonUtils.ip1 = it.toString()
                CommonUtils.uploadUrl = CommonUtils.defaultHost + it.toString()+ "." + CommonUtils.ip + CommonUtils.defaultPort
                CommonUtils.downloadUrl = CommonUtils.defaultHost + it.toString()+ "." + CommonUtils.ip + CommonUtils.defaultPort
            }

            btnContentClose.setOnClickListener {
                dismiss()
            }

            btnContentSettings.setOnClickListener {
                dismiss()
                val adminSettingDialog = AdminSettingDialog(context, apkSync) {
                    this@AdminDialog.show()
                }
                adminSettingDialog.show()
            }

            btnHqSync.setOnClickListener {
                if(etIpInfo1.text?.isEmpty() == true || etIpInfo.text?.isEmpty() == true) {
                    SimpleDialog(context, "IP를 입력해 주세요.").show()
                } else {
                    hqSync(this@AdminDialog)
                }
            }

            btnApkSync.setOnClickListener {
                apkSync(this@AdminDialog)
            }

            btnResultSync.setOnClickListener {
                if(etIpInfo1.text?.isEmpty() == true || etIpInfo.text?.isEmpty() == true) {
                    SimpleDialog(context, "IP를 입력해 주세요.").show()
                } else {
                    resultSync(this@AdminDialog)
                }
            }

            btnVideoSync.setOnClickListener {
                videoSync(this@AdminDialog)
            }

            btnDeleteHistory.setOnClickListener {
                val builder = Builder(it.context)
                builder.apply {
                    setMessage("정말로 데이터를 삭제하시겠습니까?")
                    setPositiveButton("네") { dialog, _ ->
                        dialog.dismiss()
                        deleteHistory(this@AdminDialog)
                    }
                    setNegativeButton("아니요") { dialog, _ ->
                        dialog.dismiss()
                    }
                }
                builder.create()
                builder.show()
            }

            btnDataUpdate.setOnClickListener {
                dataUpdate(this@AdminDialog)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        binding.etIpInfo.requestFocus()
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