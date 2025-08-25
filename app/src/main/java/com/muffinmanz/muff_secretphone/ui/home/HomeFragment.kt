package com.muffinmanz.muff_secretphone.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.muffinmanz.muff_secretphone.R
import com.muffinmanz.muff_secretphone.data.model.HomeListModel
import com.muffinmanz.muff_secretphone.databinding.FragmentHomeBinding
import com.muffinmanz.muff_secretphone.extensions.hide
import com.muffinmanz.muff_secretphone.extensions.observe
import com.muffinmanz.muff_secretphone.extensions.show
import com.muffinmanz.muff_secretphone.ui.adapter.InterViewerAdapter
import com.muffinmanz.muff_secretphone.ui.base.BaseFragment
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {

  override val viewModel: HomeViewModel by viewModels()
  private val args: HomeFragmentArgs by navArgs()
  private val adapter = InterViewerAdapter(::showInterViewerDialog)

  private val hMap = HashMap<String, String>()

  private lateinit var sectorsAdapter: ArrayAdapter<String>
  private lateinit var detailSectorsAdapter: ArrayAdapter<String>

  override fun init() {
    subscribeUi()
    binding.apply {
      vm = viewModel
      rvContentList.adapter = adapter

      sectorsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf("업종"))
      sectorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
      spinnerSectors.adapter = sectorsAdapter

      detailSectorsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf("기초기능특화과제"))
      detailSectorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
      spinnerDetails.adapter = detailSectorsAdapter

      viewModel.sectorsList.observe(viewLifecycleOwner) { sectors ->
        sectorsAdapter.clear()
        sectorsAdapter.add("업종")
        sectorsAdapter.addAll(sectors)
        sectorsAdapter.notifyDataSetChanged()
      }

      viewModel.detailSectorsList.observe(viewLifecycleOwner) { detailSectors ->
        detailSectorsAdapter.clear()
        detailSectorsAdapter.add("기초기능특화과제")
        detailSectorsAdapter.addAll(detailSectors)
        detailSectorsAdapter.notifyDataSetChanged()
      }

      spinnerDetails.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
          val selectedDetailsSector = if (position > 0) detailSectorsAdapter.getItem(position) else null
          viewModel.fetchSectorsList(selectedDetailsSector)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
      }

      spinnerSectors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
          val selectedSector = if (position > 0) sectorsAdapter.getItem(position) else null
          viewModel.fetchDetailSectorsList(selectedSector)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
      }

      llSearchInfo.setOnClickListener {
        if(clSearchInfo.visibility == View.VISIBLE) {
          clSearchInfo.hide()

          searchArea.text = "검색영역 열기"
          ivSearchInfo.setImageResource(R.drawable.ic_triangle_down_white)
        } else {
          clSearchInfo.show()

          searchArea.text = "검색영역 닫기"
          ivSearchInfo.setImageResource(R.drawable.ic_triangle_up_white)
        }
      }

      btnSearch.setOnClickListener {
        val selectedSector = spinnerSectors.selectedItem.toString()
        val selectedDetailSector = spinnerDetails.selectedItem.toString()

        hMap["evlDe"] = args.selecDate
        hMap["amPmSecd"] = args.selecAmPm
        hMap["qNum"] = etOffNumber.text.toString()
        hMap["birth"] = etBirth.text.toString()
        hMap["name"] = etName.text.toString()
        if(selectedSector != "업종") hMap["sector"] = selectedSector else hMap.remove("sector")
        if(selectedDetailSector != "기초기능특화과제") hMap["detailSector"] = selectedDetailSector else hMap.remove("detailSector")

        viewModel.search(hMap)
      }

      btnLogout.setOnClickListener {
        findNavController().popBackStack()
      }
    }

    val textClock = binding.tvCurrentTime
    val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.getDefault())
    val handler = Handler(Looper.getMainLooper())

    val runnable = object : Runnable {
      override fun run() {
        val currentDateTime = dateFormat.format(Date())
        textClock.text = currentDateTime
        handler.postDelayed(this, 0)
      }
    }
    handler.post(runnable)
  }

  private fun subscribeUi() {
    with(viewModel) {
      observe(fetchInterViewerList, ::fetchInterViewerList)
    }
  }

  private fun fetchInterViewerList(list: List<HomeListModel>?) {
    if (list.isNullOrEmpty()) return
    adapter.submitList(list)
  }

  private fun showInterViewerDialog(model: HomeListModel) {
    val dialog = InterViewerDialog()
    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    val adapter: JsonAdapter<HomeListModel> = moshi.adapter(HomeListModel::class.java)
    val bundle = Bundle()
    bundle.putString("model", adapter.toJson(model))
    bundle.putString("evlDe", args.selecDate)
    bundle.putString("amPmSecd", args.selecAmPm)
    dialog.arguments = bundle
    dialog.show(childFragmentManager, "InterViewerDialog")
  }
}