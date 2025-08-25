package com.muffinmanz.muff_secretphone.data.model

import androidx.camera.core.CameraSelector
import androidx.camera.video.Quality

data class CameraCapability (
  val camSelector: CameraSelector,
  val qualities: List<Quality>
)
