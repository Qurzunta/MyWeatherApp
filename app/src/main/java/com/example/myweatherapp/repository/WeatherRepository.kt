package com.example.myweatherapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myweatherapp.data.remote.WeatherApi
import com.example.myweatherapp.data.remote.WeatherResponse
import com.example.myweatherapp.utils.Constants.API_KEY
import com.example.myweatherapp.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val weatherApi: WeatherApi) {

    private val _weatherResponseLiveData = MutableLiveData<NetworkResult<WeatherResponse>>()
    val weatherResponseLiveData:LiveData<NetworkResult<WeatherResponse>>
        get() = _weatherResponseLiveData

    suspend fun getWeatherData( lat: String,
                                lon: String){
        _weatherResponseLiveData.postValue(NetworkResult.Loading())
        val response = weatherApi.getWeather(lat,lon,API_KEY)
        handleResponse(response)
    }

    private fun handleResponse(response: Response<WeatherResponse>) {
        if (response.isSuccessful && response.body() != null) {
            _weatherResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _weatherResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _weatherResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

}