package com.example.runningassistant.retrofit

import com.example.runningassistant.model.CurrentWeatherResponseModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiInterface {
    @GET("forecast?current_weather=true")
    suspend fun getCurrentWeather(@Query("longitude") longitude: Float, @Query("latitude") latitude: Float): Response<CurrentWeatherResponseModel>

    companion object {
        const val CLEAR_SKY = 0

        const val MAINLY_CLEAR = 1
        const val PARTLY_CLOUDY = 2
        const val OVERCAST = 3

        const val FOG = 45
        const val DEPOSITING_RIME_FOG = 48

        const val DRIZZLE_LIGHT = 51
        const val DRIZZLE_MODERATE = 53
        const val DRIZZLE_DENSE = 55

        const val FREEZING_DRIZZLE_LIGHT = 56
        const val FREEZING_DRIZZLE_DENSE = 57

        const val RAIN_SLIGHT = 61
        const val RAIN_MODERATE = 63
        const val RAIN_HEAVY = 65

        const val FREEZING_RAIN_LIGHT = 66
        const val FREEZING_RAIN_HEAVY = 67

        const val SNOWFALL_SLIGHT = 71
        const val SNOWFALL_MODERATE = 73
        const val SNOWFALL_HEAVY = 75

        const val SNOW_GRAINS = 77

        const val RAIN_SHOWER_SLIGHT = 80
        const val RAIN_SHOWER_MODERATE = 81
        const val RAIN_SHOWER_VIOLENT = 82

        const val SNOW_SHOWER_SLIGHT = 85
        const val SNOW_SHOWER_HEAVY = 86

        const val THUNDERSTORM = 95

        const val THUNDERSTORM_SLIGHT_HAIL = 96
        const val THUNDERSTORM_HEAVY_HAIL = 99
    }
}