package com.muffinmanz.muff_secretphone.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.muffinmanz.muff_secretphone.data.model.VideoListModel
import com.muffinmanz.muff_secretphone.databinding.ListItemVideoBinding
import com.muffinmanz.muff_secretphone.ui.adapter.diff.VideoListDiffCallback
import com.muffinmanz.muff_secretphone.utilities.CommonUtils

class VideoAdapter(private val itemClick: (VideoListModel) -> Unit) : ListAdapter<VideoListModel, RecyclerView.ViewHolder>(VideoListDiffCallback()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return VideoViewHolder(ListItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = getItem(position)
    (holder as VideoViewHolder).bind(item)
  }

  private inner class VideoViewHolder(private val binding: ListItemVideoBinding) : RecyclerView.ViewHolder(binding.root) {
    init {
      binding.setClickListener {
        binding.videoModel?.let {
          itemClick(it)
        }
      }
    }

    fun bind(item: VideoListModel) {
      binding.apply {
        videoModel = item
        CommonUtils.bindGenderCheck(tvGender, item.sex)
        CommonUtils.bindVideoCompleteTextChange(tvVCheck, item.vCheck)
        executePendingBindings()
      }
    }
  }

}