package com.example.myweatherapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myweatherapp.data.models.FavCity
import java.util.concurrent.Flow

@Dao
interface FavCitiesDao {

    @Query("SELECT * FROM tbl_FavCities ORDER BY title ASC")
    fun getFavCities(): List<FavCity>

    @Query("SELECT * FROM tbl_FavCities where title=:title")
    fun searchFavCities(title: String): List<FavCity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favCities: FavCity)

    @Query("DELETE FROM tbl_FavCities")
    suspend fun deleteAll()

    @Query("DELETE FROM tbl_FavCities where title=:title")
    suspend fun deleteFavCity(title:String)

}