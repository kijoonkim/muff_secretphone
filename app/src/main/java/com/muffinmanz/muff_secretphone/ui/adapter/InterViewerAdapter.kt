package com.muffinmanz.muff_secretphone.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.muffinmanz.muff_secretphone.data.model.HomeListModel
import com.muffinmanz.muff_secretphone.databinding.ListItemInterviewerBinding
import com.muffinmanz.muff_secretphone.ui.adapter.diff.HomeListDiffCallback
import com.muffinmanz.muff_secretphone.utilities.CommonUtils

class InterViewerAdapter(private val itemClick: (HomeListModel) -> Unit) : ListAdapter<HomeListModel, RecyclerView.ViewHolder>(HomeListDiffCallback()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return InterViewerViewHolder(ListItemInterviewerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = getItem(position)
    (holder as InterViewerViewHolder).bind(item)
  }

  private inner class InterViewerViewHolder(private val binding: ListItemInterviewerBinding) : RecyclerView.ViewHolder(binding.root) {
    init {
      binding.setClickListener {
        binding.interViewer?.let {
          itemClick(it)
        }
      }
    }

    fun bind(item: HomeListModel) {
      binding.apply {
        interViewer = item
        CommonUtils.bindImageFromString(ivIntervewer, item.img)
        CommonUtils.bindGenderCheck(tvGender, item.sex)
        CommonUtils.bindChangeText(tvAttend, item.iCheck)
        executePendingBindings()
      }
    }
  }
}