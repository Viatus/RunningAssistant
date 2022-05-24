package com.example.runningassistant.model

import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponseModel(
    @SerializedName("utc_offset_seconds")
    val utcOffsetSeconds: Int,
    @SerializedName("latitude")
    val latitude: Float,
    @SerializedName("longitude")
    val longitude: Float,
    @SerializedName("generationtime_ms")
    val generationTimeMS: Float,
    @SerializedName("current_weather")
    val currentWeather: CurrentWeatherModel,
    @SerializedName("elevation")
    val elevation: Float
)

data class CurrentWeatherModel(
    @SerializedName("windspeed")
    val windSpeed: Float,
    @SerializedName("temperature")
    val temperature: Float,
    @SerializedName("winddirection")
    val windDirection: Int,
    @SerializedName("weathercode")
    val weatherCode: Int,
    @SerializedName("time")
    val time: String,
)