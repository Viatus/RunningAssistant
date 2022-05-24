package com.example.runningassistant.retrofit

import com.example.runningassistant.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    const val WEATHER_API_URL = "https://api.open-meteo.com/v1/"

    val retrofitClient: Retrofit.Builder by lazy {

        val levelType: HttpLoggingInterceptor.Level
        if (BuildConfig.BUILD_TYPE.contentEquals("debug"))
            levelType = HttpLoggingInterceptor.Level.BODY else levelType =
            HttpLoggingInterceptor.Level.NONE

        val logging = HttpLoggingInterceptor()
        logging.setLevel(levelType)

        val okhttpClient = OkHttpClient.Builder()
        okhttpClient.addInterceptor(logging)

        Retrofit.Builder()
            .baseUrl(WEATHER_API_URL)
            .client(okhttpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
    }

    val apiInterface: WeatherApiInterface by lazy {
        retrofitClient.build().create(WeatherApiInterface::class.java)
    }
}