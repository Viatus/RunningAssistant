package com.example.runningassistant.repository

import androidx.lifecycle.MutableLiveData
import com.example.runningassistant.model.CurrentWeatherResponseModel
import com.example.runningassistant.retrofit.RetrofitClient

object RetrofitRepo {
    val currentWeatherResponseLiveData = MutableLiveData<CurrentWeatherResponseModel>()

    suspend fun getCurrentWeather(longitude:Float, latitude: Float) = RetrofitClient.apiInterface.getCurrentWeather(longitude, latitude)
}