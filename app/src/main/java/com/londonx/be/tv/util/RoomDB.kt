package com.londonx.be.tv.util

import androidx.room.*
import com.londonx.be.tv.BuildConfig
import com.londonx.be.tv.entity.TVStation
import splitties.init.appCtx

val db by lazy {
    Room.databaseBuilder(appCtx.applicationContext, AppDatabase::class.java, "app_db")
        .also {
            if (BuildConfig.DEBUG) {
                it.fallbackToDestructiveMigration()
            }
        }
        .build()
}

@Database(
    entities = [TVStation::class],//Entity::class
    version = 1
)

abstract class AppDatabase : RoomDatabase() {
    //abstract fun entityDao(): EntityDao
    abstract fun tvStationDao(): TVStationDao
}

@Dao
interface TVStationDao {

    @Insert
    suspend fun insert(vararg tvStations: TVStation)

    @Delete
    suspend fun remove(vararg tvStations: TVStation)

    @Update
    suspend fun update(vararg tvStations: TVStation)

    @Query("SELECT * FROM TVStation ORDER BY sort ASC")
    suspend fun getAll(): List<TVStation>

}