package com.example.myweatherapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_FavCities")
data class FavCity(@PrimaryKey val title:String, var lat:String, var lon:String)