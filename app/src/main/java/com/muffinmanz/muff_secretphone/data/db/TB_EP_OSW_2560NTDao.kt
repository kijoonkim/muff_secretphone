package com.muffinmanz.muff_secretphone.data.db

import androidx.room.*
import com.muffinmanz.muff_secretphone.data.model.HomeListModel
import com.muffinmanz.muff_secretphone.data.model.VideoListModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TB_EP_OSW_2560NTDao {
    @Query("SELECT MAX(SPNTSEQ) FROM TB_EP_OSW_2560NT")
    suspend fun getMaxSpntSeq() : Int

    @Query("SELECT MAX(GRDRSEQ) FROM TB_EP_OSW_2560NT")
    suspend fun getMaxGrdrSeq() : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTbEpOsw2560Dao(list: List<TB_EP_OSW_2560NT>)

    @Query(
        "SELECT * " +
                "FROM TB_EP_OSW_2560NT "
    )
    suspend fun fetchTbEpOsw2560List(): List<TB_EP_OSW_2560NT>?
}