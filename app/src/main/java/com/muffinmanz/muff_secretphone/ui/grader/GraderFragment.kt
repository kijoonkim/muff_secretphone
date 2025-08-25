package com.muffinmanz.muff_secretphone.ui.grader

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager

import com.muffinmanz.muff_secretphone.R
import com.muffinmanz.muff_secretphone.data.model.VideoListModel
import com.muffinmanz.muff_secretphone.databinding.FragmentGraderBinding
import com.muffinmanz.muff_secretphone.dialo.SelectSimpleDialog
import com.muffinmanz.muff_secretphone.dialog.RemindDialog
import com.muffinmanz.muff_secretphone.dialog.SimpleDialog
import com.muffinmanz.muff_secretphone.dialog.VideoInfoDialog
import com.muffinmanz.muff_secretphone.extensions.observe
import com.muffinmanz.muff_secretphone.ui.adapter.VideoAdapter
import com.muffinmanz.muff_secretphone.ui.base.BaseFragment
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GraderFragment : BaseFragment<FragmentGraderBinding, GraderViewModel>(R.layout.fragment_grader) {
    override val viewModel: GraderViewModel by viewModels()
    private val args: GraderFragmentArgs by navArgs()
    private val videoAdapter = VideoAdapter(::adapterItemClick)
    private var lastScrollPosition: Int = 0
    private var lastScrollOffset: Int = 0

    val hMap = HashMap<String, String>()

    private lateinit var sectorsAdapter: ArrayAdapter<String>
    private lateinit var detailSectorsAdapter: ArrayAdapter<String>
    private lateinit var vCheckAdapter: ArrayAdapter<String>

    override fun init() {
        subscribeUi()
        binding.apply {
            vm = viewModel
            rvContentList.adapter = videoAdapter

            id.text = "ID: ${args.txtId}"

            sectorsAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                mutableListOf("업종")
            )
            sectorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSectors.adapter = sectorsAdapter

            detailSectorsAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                mutableListOf("기초기능특화과제")
            )
            detailSectorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDetails.adapter = detailSectorsAdapter

            vCheckAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                listOf("촬영여부 전체", "완료", "대기")
            )
            vCheckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerVCheck.adapter = vCheckAdapter

            viewModel.sectorsList.observe(viewLifecycleOwner) { sectors ->
                sectorsAdapter.clear()
                sectorsAdapter.add("업종")
                sectorsAdapter.addAll(sectors)
                sectorsAdapter.notifyDataSetChanged()
            }

            viewModel.detailSectorsList.observe(viewLifecycleOwner) { sectors ->
                detailSectorsAdapter.clear()
                detailSectorsAdapter.add("기초기능특화과제")
                detailSectorsAdapter.addAll(sectors)
                detailSectorsAdapter.notifyDataSetChanged()
            }

            spinnerDetails.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedDetailsSector = if (position > 0) detailSectorsAdapter.getItem(position) else null
                    viewModel.fetchSectorsList(selectedDetailsSector)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            spinnerSectors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedSector = if (position > 0) sectorsAdapter.getItem(position) else null
                    viewModel.fetchDetailSectorsList(selectedSector)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            saveRecyclerViewScrollPosition()

            if(listMoveCheck.isChecked) {
                viewModel.fetchFlowVideoListSorted()
            } else {
                viewModel.fetchFlowVideoList()
            }

            btnSearch.setOnClickListener {
                val selectedSector = spinnerSectors.selectedItem.toString()
                val selectedDetailSector = spinnerDetails.selectedItem.toString()
                val selectedVCheck = spinnerVCheck.selectedItem.toString()

                hMap["evlDe"] = args.selecDate
                hMap["amPmSecd"] = args.selecAmPm
                hMap["qNum"] = etOffNumberName.text.toString()
                hMap["name"] = etOffNumberName.text.toString()
                if (selectedSector != "업종") hMap["sector"] = selectedSector else hMap.remove("sector")
                if (selectedDetailSector != "기초기능특화과제") hMap["detailSector"] = selectedDetailSector else hMap.remove("detailSector")
                if (selectedVCheck != "촬영여부 전체") hMap["vCheck"] = selectedVCheck else hMap.remove("vCheck")

                viewModel.search(hMap)
            }

            btnLogout.setOnClickListener {
                findNavController().popBackStack()
            }

            btnVideoSave.setOnClickListener {
                val selectSimpleDialog = context?.let { it1 -> SelectSimpleDialog(it1, "저장 하시겠습니까?") }
                if (selectSimpleDialog != null) {
                    selectSimpleDialog.show()

                    selectSimpleDialog.setOnSaveClickListener {
                        saveRecyclerViewScrollPosition()

                        if(listMoveCheck.isChecked) {
                            viewModel.fetchFlowVideoListSorted()
                        } else {
                            viewModel.fetchFlowVideoList()
                        }
                        context?.let { it1 -> SimpleDialog(it1, "면접 영상이 저장되었습니다.").show() }
                    }
                }
            }

            // 체크여부에 따른 실시간 목록 정렬 (필요 시 사용)
//            listMoveCheck.setOnCheckedChangeListener { buttonView, isChecked ->
//                saveRecyclerViewScrollPosition()
//
//                if(isChecked) {
//                    viewModel.fetchFlowVideoListSorted()
//                } else {
//                    viewModel.fetchFlowVideoList()
//                }
//            }
        }
        observeViewModel()
    }
    private fun observeViewModel() {
        viewModel.fetchVideoList.observe(viewLifecycleOwner) {
            saveRecyclerViewScrollPosition()
            binding.rvContentList.adapter?.notifyDataSetChanged()

            binding.rvContentList.postDelayed({
                restoreRecyclerViewScrollPosition()
            }, 100)
        }
    }

    private fun saveRecyclerViewScrollPosition() {
        val layoutManager = binding.rvContentList.layoutManager as? LinearLayoutManager
        layoutManager?.let {
            lastScrollPosition = it.findFirstVisibleItemPosition()
            val firstItemView = it.findViewByPosition(lastScrollPosition)
            lastScrollOffset = firstItemView?.top ?: 0
        }
    }

    private fun restoreRecyclerViewScrollPosition() {
        binding.rvContentList.post {
            val layoutManager = binding.rvContentList.layoutManager as? LinearLayoutManager
            layoutManager?.scrollToPositionWithOffset(lastScrollPosition, lastScrollOffset)
        }
    }

    private fun subscribeUi() {
        with(viewModel) {
            observe(fetchVideoList, ::fetchVideoList)
        }
    }

    private fun fetchVideoList(list: List<VideoListModel>?) {
        if(list.isNullOrEmpty()) return
        videoAdapter.submitList(list)
    }

    private fun adapterItemClick(model: VideoListModel) {
        if(model.videoPath.isNullOrEmpty()) {
            val dialog = RemindDialog(requireContext(), model, {
                it.dismiss()

                val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
                val adapter: JsonAdapter<VideoListModel> = moshi.adapter(VideoListModel::class.java)
                navigateToVideo(adapter.toJson(model), false)
            }, {
                it.dismiss()

                val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
                val adapter: JsonAdapter<VideoListModel> = moshi.adapter(VideoListModel::class.java)
                navigateToVideo(adapter.toJson(model), true)
            })

            if(!RemindDialog.showCheck) dialog.show()
        } else {
            val dialog = VideoInfoDialog(requireContext(), model) {
                it.dismiss()

                val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
                val adapter: JsonAdapter<VideoListModel> = moshi.adapter(VideoListModel::class.java)
                navigateToVideo(adapter.toJson(model), false)
            }
            if(!VideoInfoDialog.showCheck) dialog.show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(findNavController().currentBackStackEntry != null) {
            val backStateEntry = findNavController().currentBackStackEntry!!

            val observerEvent = LifecycleEventObserver { owner, event ->
                if(event != Lifecycle.Event.ON_RESUME) {
                    val result = backStateEntry.savedStateHandle.get<String>("listSubscriber");

                    if(result != null) {
                        try {
                            viewLifecycleOwner.lifecycleScope.launch {
                                this.coroutineContext.let {
                                    if(it.isActive) {
                                        owner.withCreated {
                                            subscribeUi()
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            }

            backStateEntry.lifecycle.removeObserver(observerEvent)
            backStateEntry.lifecycle.addObserver(observerEvent)
        }
    }

    private fun navigateToVideo(model: String, isEnglish: Boolean) {
        val directions = GraderFragmentDirections.actionGraderFragmentToVideoFragment(model, isEnglish)

        if (findNavController().currentDestination?.id == R.id.graderFragment) {
            findNavController().navigate(directions)
        }
    }
}