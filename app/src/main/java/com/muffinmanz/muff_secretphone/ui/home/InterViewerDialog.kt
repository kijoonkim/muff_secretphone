package com.muffinmanz.muff_secretphone.ui.home

import androidx.fragment.app.viewModels

import com.muffinmanz.muff_secretphone.R
import com.muffinmanz.muff_secretphone.data.model.HomeListModel
import com.muffinmanz.muff_secretphone.databinding.DialogInterviewerBinding
import com.muffinmanz.muff_secretphone.extensions.hide
import com.muffinmanz.muff_secretphone.extensions.observe
import com.muffinmanz.muff_secretphone.extensions.show
import com.muffinmanz.muff_secretphone.ui.base.BaseDialogFragment
import com.muffinmanz.muff_secretphone.utilities.CommonUtils
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InterViewerDialog : BaseDialogFragment<DialogInterviewerBinding, HomeViewModel>(R.layout.dialog_interviewer) {
	private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

	override val viewModel: HomeViewModel by viewModels()

	private val initModel: HomeListModel? by lazy {
		val adapter: JsonAdapter<HomeListModel> = moshi.adapter(HomeListModel::class.java)
		val modelString = requireArguments().getString("model")
		modelString?.let { adapter.fromJson(it) }
	}

	override fun init() {
		val evlDe = requireArguments().getString("evlDe") ?: ""
		val amPmSecd = requireArguments().getString("amPmSecd") ?: ""

		viewModel.fetchOffNum.value = initModel?.offNumber.toString()
		viewModel.fetchSkillNum.value = initModel?.skillNum.toString()
		subscribeUi()
		binding.apply {
			homeModel = initModel

			updateButtonVisibility(initModel?.iCheck)

			CommonUtils.bindPosVisible(btnExamination, initModel?.iCheck)
			CommonUtils.bindNegVisible(btnAbsent, initModel?.iCheck)
			CommonUtils.bindImageFromString(ivUser, initModel?.img)
			CommonUtils.bindGenderCheck(tvGenderInfo, initModel?.sex)

			btnClose.setOnClickListener {
				dismiss()
			}
			btnGenderChg.setOnClickListener {
				val updatedGender = if (tvGenderInfo.text.toString() == "남자") "21202" else "21201"
				val updtExamNo = viewModel.fetchModel.value?.examNo ?: initModel?.examNo.toString()

				tvGenderInfo.text = if (updatedGender == "21201") "남자" else "여자"

				viewModel.updateGender(updatedGender, updtExamNo)
			}
			btnExamination.setOnClickListener {
				val updtExamNo = viewModel.fetchModel.value?.examNo ?: initModel?.examNo.toString()

				viewModel.updateICheck("Y", updtExamNo, evlDe, amPmSecd)

				updateButtonVisibility("Y")
			}
			btnAbsent.setOnClickListener {
				val updtExamNo = viewModel.fetchModel.value?.examNo ?: initModel?.examNo.toString()

				viewModel.updateICheck("N", updtExamNo, evlDe, amPmSecd)

				updateButtonVisibility("N")
			}
			btnReset.setOnClickListener {
				val updtExamNo = viewModel.fetchModel.value?.examNo ?: initModel?.examNo.toString()

				viewModel.updateICheck("", updtExamNo, evlDe, amPmSecd)

				updateButtonVisibility("")
			}
			btnPrev.setOnClickListener {
				viewModel.fetchPrev(evlDe, amPmSecd)
			}
			btnNext.setOnClickListener {
				viewModel.fetchNext(evlDe, amPmSecd)
			}
		}
	}

	private fun subscribeUi() {
		with(viewModel) {
			observe(fetchModel, ::fetchNextModel)
		}
	}

	private fun fetchNextModel(model: HomeListModel?) {
		if(model == null) return
		binding.apply {
			homeModel = model

			CommonUtils.bindPosVisible(btnExamination, model.iCheck)
			CommonUtils.bindNegVisible(btnAbsent, model.iCheck)
			CommonUtils.bindImageFromString(ivUser, model.img)
			CommonUtils.bindGenderCheck(tvGenderInfo, model.sex)
		}
	}

	private fun updateButtonVisibility(iCheck: String?) {
		binding.apply {
			if(iCheck.isNullOrEmpty()) {
				btnExamination.show()
				btnAbsent.show()
			} else {
				if(iCheck.equals("Y", true)) {
					btnExamination.hide()
					btnAbsent.show()
				} else if(iCheck.equals("N", true)) {
					btnExamination.show()
					btnAbsent.hide()
				}
			}
		}
	}
}