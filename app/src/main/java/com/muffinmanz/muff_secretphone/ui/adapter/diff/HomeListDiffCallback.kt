package com.muffinmanz.muff_secretphone.ui.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.muffinmanz.muff_secretphone.data.model.HomeListModel

class HomeListDiffCallback : DiffUtil.ItemCallback<HomeListModel>() {

  override fun areItemsTheSame(oldItem: HomeListModel, newItem: HomeListModel): Boolean {
    return oldItem.examNo == newItem.examNo
  }

  override fun areContentsTheSame(oldItem: HomeListModel, newItem: HomeListModel): Boolean {
    return oldItem == newItem
  }
}