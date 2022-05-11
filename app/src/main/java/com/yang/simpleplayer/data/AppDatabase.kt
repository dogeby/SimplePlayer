package com.yang.simpleplayer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yang.simpleplayer.models.Playlist
import com.yang.simpleplayer.models.PlaylistVideoInfoCrossRef
import com.yang.simpleplayer.models.VideoInfo

@Database(entities = [VideoInfo::class, Playlist::class, PlaylistVideoInfoCrossRef::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract  fun videoInfoDbDao():VideoInfoDbDao
    abstract  fun playlistDbDao():PlaylistDbDao

    companion object {
        private var dbInstance: AppDatabase? = null

        fun getDatabase(context:Context): AppDatabase {
            return dbInstance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                dbInstance = instance
                instance
            }
        }
    }

}