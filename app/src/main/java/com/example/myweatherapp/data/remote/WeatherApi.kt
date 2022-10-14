package com.example.myweatherapp.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi{

    @GET("/data/3.0/onecall?")
    suspend fun getWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appId: String,
        @Query("units") unit: String = "metric",
        @Query("exclude") exclude: String = "minutely,hourly,alerts"

    ): Response<WeatherResponse>


}