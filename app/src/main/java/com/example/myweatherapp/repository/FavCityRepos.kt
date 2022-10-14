package com.example.myweatherapp.repository

import androidx.annotation.WorkerThread
import com.example.myweatherapp.dao.FavCitiesDao
import com.example.myweatherapp.data.models.FavCity

class FavCityRepos(private val favCitiesDao: FavCitiesDao) {
    val favcityist: List<FavCity> = favCitiesDao.getFavCities()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(favCity: FavCity){
        favCitiesDao.insert(favCity)
    }
}