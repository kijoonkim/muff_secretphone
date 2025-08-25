package com.muffinmanz.muff_secretphone.ui.login

import android.app.DatePickerDialog
import android.content.pm.PackageInfo
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast

import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.activity.OnBackPressedCallback

import com.muffinmanz.muff_secretphone.R
import com.muffinmanz.muff_secretphone.ZIP_FOLDER
import com.muffinmanz.muff_secretphone.data.prefs.AppPreferencesHelper
import com.muffinmanz.muff_secretphone.databinding.FragmentLoginBinding
import com.muffinmanz.muff_secretphone.dialo.SelectSimpleDialog
import com.muffinmanz.muff_secretphone.dialog.AdminDialog
import com.muffinmanz.muff_secretphone.dialog.PasswordDialog
import com.muffinmanz.muff_secretphone.dialog.SimpleDialog
import com.muffinmanz.muff_secretphone.extensions.observe
import com.muffinmanz.muff_secretphone.ui.base.BaseFragment
import com.muffinmanz.muff_secretphone.utilities.DateUtil

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch

import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import kotlin.math.abs

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel> (R.layout.fragment_login) {
    override val viewModel: LoginViewModel by viewModels()

    private val TAG = this.javaClass.simpleName;

    private var isSwipe = true
    private var selecDate: String = "";
    private var selecAmPm: String = "";
    private var backPressedTime: Long = 0

    private lateinit var gesture: GestureDetectorCompat

    private val gestureListener = object : GestureDetector.OnGestureListener {
        override fun onDown(p0: MotionEvent) = false
        override fun onShowPress(p0: MotionEvent) = Unit
        override fun onSingleTapUp(p0: MotionEvent) = false
        override fun onLongPress(p0: MotionEvent) = Unit

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, p2: Float, p3: Float) : Boolean {
            if(e2.pointerCount == 2) {
                val deltaY = e2.y - e1!!.y

                if(abs(deltaY) > 80) {
                    if(deltaY > 0 && isSwipe) {
                        isSwipe = false
                        showPasswordDialog(selecDate.toString(), selecAmPm.toString())
                    }
                }
            }
            return false
        }

        override fun onFling(p0: MotionEvent?, p1: MotionEvent, p2: Float, p3: Float) : Boolean {
            isSwipe = true
            return false
        }
    }

    override fun init() {
        subscribeUi()
        fetchVersion()
        gesture = GestureDetectorCompat(requireContext(), gestureListener)
        binding.root.setOnTouchListener { view, motionEvent ->
            gesture.onTouchEvent(motionEvent)
            view.performClick()
            true
        }
        val prefsHelper = AppPreferencesHelper(
            requireContext(),
            requireContext().getString(R.string.preferences_key)
        )
        prefsHelper.resolutionIndex = 2

        binding.apply {
            val pInfo: PackageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            tvTextVersion.text = pInfo.versionName

            //아이디 공백 제거
            etLoginId.addTextChangedListener(object: TextWatcher {
                private var previousText = ""

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(previousText != s?.toString()) {
                        val filteredText = s?.toString()?.replace(" ", "") ?: ""
                        previousText = filteredText
                        etLoginId.setText(filteredText)
                        etLoginId.setSelection(filteredText.length) // 커서를 마지막으로 이동
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            //Am or Pm 클릭 이벤트 설정
            rbSelecAm.setOnClickListener {
                selecAmPm = rbSelecAm.text.toString()
            }
            rbSelecPm.setOnClickListener {
                selecAmPm = rbSelecPm.text.toString()
            }

            btnLogin.setOnClickListener {
                if(selecDate == "") {
                    val dialog = SimpleDialog(requireContext(), "시행일을 선택 하세요.")
                    dialog.show()
                } else if(selecAmPm == "") {
                    val dialog = SimpleDialog(requireContext(), "세션을 선택 하세요.")
                    dialog.show()
                } else {
                    val dbDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.KOREAN)

                    if(etLoginId.text.toString() == "admin" && etLoginPw.text.toString() == "1234") {
                        viewModel.insertLoginLog("admin", dbDateFormat.format(Date()))
                        navigateToHome(selecDate, selecAmPm)
                    } else if(etLoginId.text.toString() == "ksr" && etLoginPw.text.toString() == "1234") {
                        viewModel.insertLoginLog("ksr", dbDateFormat.format(Date()))
                        navigateToGrader(selecDate, selecAmPm)
                    } else {
                        Log.d(TAG, "Login Info Dialog");

                        val dialog = SimpleDialog(requireContext(), "로그인 정보를 확인바랍니다.")
                        dialog.show()
                    }
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(System.currentTimeMillis() - backPressedTime < 2000) {
                    requireActivity().finish()
                } else {
                    backPressedTime = System.currentTimeMillis()
                    Toast.makeText(requireContext(), "\'뒤로\' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun subscribeUi() {
        with(viewModel) {
            fetchTbEpOsw2110Nt.observe(viewLifecycleOwner, ::fetchLoginInfo)
        }
    }

    private fun fetchLoginInfo(infoMap: HashMap<String, String>) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val oprnTitle = infoMap["oprnTitle"]
        val oprnDate = infoMap["oprnDate"]
        val examDateStr = infoMap["examDateStr"]
        val oprnTme = infoMap["oprnTme"]
        val oprnTmeDts = infoMap["oprnTmeDts"]
        val amPm = infoMap["amPm"]
        val startDate = infoMap["startDate"]
        val endDate = infoMap["endDate"]

        binding.tvLoginTitle.text = oprnTitle
        binding.tvSelecDate.text = oprnDate
        binding.tvDate.text = examDateStr
        binding.tvTme.text = oprnTme + "차"
        binding.tvTmeDts.text = "세부회자: ${oprnTme}-${oprnTmeDts}"

        selecDate = oprnDate.toString()
        selecAmPm = amPm.toString()
        if(selecAmPm === "am") {
            binding.rgSelecAmPm.check(binding.rbSelecAm.id)
        } else {
            binding.rgSelecAmPm.check(binding.rbSelecPm.id)
        }

        //시행일 선택 달력 시작
        binding.tvSelecDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(requireContext(), { _, year1, month1, dayOfMonth ->
                    val formattedDay = if(dayOfMonth < 10) {
                        "0$dayOfMonth"
                    } else {
                        "$dayOfMonth"
                    }
                    val formattedMonth = if((month1 + 1) < 10) {
                        "0${month1 + 1}"
                    } else {
                        "${month1 + 1}"
                    }

                    selecDate = "$year1-$formattedMonth-$formattedDay"
                    binding.tvSelecDate.text = selecDate
                }, year, month, day)

            val arrStartDate = DateUtil.getArrDateFromString(startDate, "-")
            val arrEndDate = DateUtil.getArrDateFromString(endDate, "-")
            if(arrStartDate != null && arrEndDate != null) {
                val minDate = Calendar.getInstance().apply {
                    set(
                        Integer.parseInt(arrStartDate[0]),
                        Integer.parseInt(arrStartDate[1]) - 1,
                        Integer.parseInt(arrStartDate[2])
                    )
                }.timeInMillis

                val maxDate = Calendar.getInstance().apply {
                    set(
                        Integer.parseInt(arrEndDate[0]),
                        Integer.parseInt(arrEndDate[1]) - 1,
                        Integer.parseInt(arrEndDate[2])
                    )
                }.timeInMillis

                datePickerDialog.datePicker.minDate = minDate
                datePickerDialog.datePicker.maxDate = maxDate
            }
            datePickerDialog.show()
        }
        //시행일 선택 달력 끝.
    }

    private fun fetchVersion() {
        val versionFile = File(ZIP_FOLDER, "version.txt")

        if(versionFile.exists()) {
            binding.tvTextTrial.text = versionFile.readText()
        }
    }

    private fun showPasswordDialog(evlDe: String, amPmSecd: String) {
        val dialog = PasswordDialog(requireContext()) {
            showAdminDialog(evlDe, amPmSecd)
            it.dismiss()
        }

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        if(!PasswordDialog.showCheck) {
            dialog.show()
        }
    }

    private fun showAdminDialog(evlDe: String, amPmSecd: String) {
        val dialog = AdminDialog(requireContext(), viewModel, { hqSync ->
            navigateToDownload()
            hqSync.dismiss()
        }, { resultSync ->
            val selectSimpleDialog = context?.let { SelectSimpleDialog(it, "응시 결과를 업로드 하시겠습니까?\n(선택된 시험일자: " + evlDe + " " + amPmSecd + ")") }
            if (selectSimpleDialog != null) {
                selectSimpleDialog.show()
                selectSimpleDialog.setOnSaveClickListener {
                    viewModel.jsonFileUpload(
                        evlDe,
                        amPmSecd,
                        requireContext().getString(R.string.result_upload_log)
                    )
                }
            }
            resultSync.dismiss()
        }, { apkSync ->
            viewModel.fetchDownload { percent ->
                lifecycleScope.launch {
                    //uploadProgress.progress = percent
                    //tvResultInfo.text = StringBuilder("$percent/100")
                }
            }
            //viewModel.jsonFileUpload()
            apkSync.dismiss()
        }, { videoSync ->
            viewModel.jsonFileUpload(
                evlDe,
                amPmSecd,
                requireContext().getString(R.string.video_upload_log)
            )
            // viewModel.videoFileUpload()
            videoSync.dismiss()
        }, { deleteHistory ->
            viewModel.deleteHistory()
            deleteHistory.dismiss()
        }, { dataUpdate ->
            viewModel.insertExtDatabase()
        })

        dialog.setOnDismissListener {
            viewModel.insertIpConfig()
        }

        if(!AdminDialog.showCheck) {
            dialog.show()
        }
    }

    private fun navigateToHome(selecDate: String, selecAmPm: String) {
        val directions = LoginFragmentDirections.actionLoginFragmentToHomeFragment(selecDate, selecAmPm)

        findNavController().navigate(directions)
    }

    private fun navigateToGrader(selecDate: String, selecAmPm: String) {
        val txtId = binding.etLoginId.text.toString()
        val directions = LoginFragmentDirections.actionLoginFragmentToGraderFragment(selecDate, selecAmPm, txtId)

        findNavController().navigate(directions)
    }

    private fun navigateToDownload() {
        val directions = LoginFragmentDirections.actionLoginFragmentToDownloadFragment()

        findNavController().navigate(directions)
    }
}