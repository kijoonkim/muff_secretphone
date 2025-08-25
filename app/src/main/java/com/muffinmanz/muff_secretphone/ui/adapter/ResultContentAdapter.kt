package com.muffinmanz.muff_secretphone.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.muffinmanz.muff_secretphone.data.model.VideoListModel
import com.muffinmanz.muff_secretphone.databinding.ListItemResultContentBinding
import com.muffinmanz.muff_secretphone.ui.adapter.diff.VideoListDiffCallback
import com.muffinmanz.muff_secretphone.utilities.CommonUtils

class ResultContentAdapter : ListAdapter<VideoListModel, RecyclerView.ViewHolder>(VideoListDiffCallback()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return ResultContentViewHolder(ListItemResultContentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = getItem(position)
    (holder as ResultContentViewHolder).bind(item)
  }

  private class ResultContentViewHolder(private val binding: ListItemResultContentBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: VideoListModel) {
      binding.apply {
        interViewer = item
        CommonUtils.bindImageFromString(ivUser, item.img)
        executePendingBindings()
      }
    }
  }
}