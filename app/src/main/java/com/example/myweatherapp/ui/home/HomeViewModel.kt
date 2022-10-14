package com.example.myweatherapp.ui.home

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweatherapp.data.remote.WeatherResponse
import com.example.myweatherapp.repository.WeatherRepository
import com.example.myweatherapp.utils.Constants
import com.example.myweatherapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val weatherRepository: WeatherRepository) : ViewModel() {



    val weatherLiveData get() = weatherRepository.weatherResponseLiveData

    init{
        getWeather()
    }

    fun getWeather(lat:String = Constants.LATITUDE ,lon:String = Constants.LONGITUDE){
        viewModelScope.launch {
            weatherRepository.getWeatherData(lat,lon)
        }
    }

}