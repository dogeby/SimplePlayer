package com.yang.simpleplayer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yang.simpleplayer.models.VideoInfo

@Database(entities = [VideoInfo::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class VideoInfoDatabase : RoomDatabase() {
    abstract  fun videoDbDao():VideoInfoDbDao

    companion object {
        private var dbInstance: VideoInfoDatabase? = null

        fun getDatabase(context:Context): VideoInfoDatabase {
            return dbInstance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VideoInfoDatabase::class.java,
                    "video_info_database"
                ).build()
                dbInstance = instance
                instance
            }
        }
    }

}