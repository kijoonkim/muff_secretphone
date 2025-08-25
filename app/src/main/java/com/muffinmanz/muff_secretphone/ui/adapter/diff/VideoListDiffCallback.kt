package com.muffinmanz.muff_secretphone.ui.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.muffinmanz.muff_secretphone.data.model.VideoListModel

class VideoListDiffCallback : DiffUtil.ItemCallback<VideoListModel>() {

  override fun areItemsTheSame(oldItem: VideoListModel, newItem: VideoListModel): Boolean {
    return oldItem.examNo == newItem.examNo
  }

  override fun areContentsTheSame(oldItem: VideoListModel, newItem: VideoListModel): Boolean {
    return oldItem == newItem
  }
}