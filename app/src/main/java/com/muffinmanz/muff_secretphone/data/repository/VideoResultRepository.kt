package com.muffinmanz.muff_secretphone.data.repository

import androidx.annotation.WorkerThread
import com.muffinmanz.muff_secretphone.data.db.VideoRecData
import com.muffinmanz.muff_secretphone.data.db.VideoRecDataDao
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VideoResultRepository @Inject constructor(private val videoRecDataDao: VideoRecDataDao) {

  suspend fun updateVideoRecInfo(video: VideoRecData) = videoRecDataDao.insertVideoRec(video)
}