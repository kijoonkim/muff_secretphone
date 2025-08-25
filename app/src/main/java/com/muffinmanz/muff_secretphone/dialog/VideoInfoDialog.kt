package com.muffinmanz.muff_secretphone.dialog

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.util.EventLogger
import com.muffinmanz.muff_secretphone.R
import com.muffinmanz.muff_secretphone.data.model.VideoListModel
import com.muffinmanz.muff_secretphone.databinding.DialogVideoInfoBinding
import com.muffinmanz.muff_secretphone.utilities.CommonUtils
import java.io.File

class VideoInfoDialog(context: Context, private val model: VideoListModel, private val reShoot: (AlertDialog) -> Unit) : AlertDialog(context, R.style.AlertDialogLightX) {

  private lateinit var binding: DialogVideoInfoBinding

  private var playerView: StyledPlayerView? = null
  private var player: ExoPlayer? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DialogVideoInfoBinding.inflate(layoutInflater)
    setContentView(binding.root)

    playerView = binding.playerView

    binding.apply {
      interViewer = model

      CommonUtils.bindImageFromString(ivUser, model.img)

      playerView.apply {
        requestFocus()
      }

      btnConfirm.setOnClickListener {
        dismiss()
      }

      btnReShoot.setOnClickListener {
        reShoot(this@VideoInfoDialog)
      }
    }
  }

  private fun initializePlayer(): Boolean {
    if (player == null) {

      val mediaItem = MediaItem.Builder().setUri(File(model.videoPath!!).toUri()).build()

      player = ExoPlayer.Builder(context).build()
      player!!.setMediaItem(mediaItem)
      player!!.setAudioAttributes(AudioAttributes.DEFAULT, true)
      player!!.playWhenReady = true
      playerView!!.player = player
    }
    player!!.prepare()
    return true
  }

  override fun show() {
    super.show()
    showCheck = true
  }

  override fun dismiss() {
    super.dismiss()
    showCheck = false
  }

  override fun onStart() {
    super.onStart()
    initializePlayer()
    if (playerView != null) {
      playerView!!.onResume()
    }
  }

  override fun onStop() {
    super.onStop()
    showCheck = false
    if (playerView != null) {
      playerView!!.onPause()
    }
    releasePlayer()
  }

  private fun releasePlayer() {
    if (player != null) {
      player!!.release()
      player = null
      playerView!!.player = null
    }
  }

  companion object {
    var showCheck = false
  }
}