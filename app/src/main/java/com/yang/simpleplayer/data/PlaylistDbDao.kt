package com.yang.simpleplayer.data

import androidx.room.*
import com.yang.simpleplayer.models.Playlist

@Dao
interface PlaylistDbDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(playlist: Playlist)

    @Update
    suspend fun update(playlist: Playlist)

    @Delete
    suspend fun delete(playlist: Playlist)

    @Query("SELECT video_id, name FROM Playlist WHERE name = :name")
    suspend fun getPlaylistVideoIds(name:String):List<Playlist>

    @Query("SELECT name FROM Playlist GROUP BY  name")
    suspend fun getPlaylistsName():List<String>
}