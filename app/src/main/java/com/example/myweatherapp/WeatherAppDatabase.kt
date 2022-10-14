package com.example.myweatherapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myweatherapp.dao.FavCitiesDao
import com.example.myweatherapp.data.models.FavCity

@Database(entities = arrayOf(FavCity::class), version = 1, exportSchema = false)
public abstract class WeatherAppDatabase:RoomDatabase() {

    abstract fun favCitiesDao(): FavCitiesDao

    companion object{

        @Volatile
        private var INSTANCE: WeatherAppDatabase?=null
        fun getDatabase(context: Context):WeatherAppDatabase{

            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                WeatherAppDatabase::class.java,"WeatherApp").
                    allowMainThreadQueries().
                    build()
                INSTANCE = instance
                instance
            }

        }
    }


}