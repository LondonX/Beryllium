package com.londonx.be.tv.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TVStation(
    @ColumnInfo val title: String,
    @ColumnInfo val m3u8: String,
    @ColumnInfo val sort: Int,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)