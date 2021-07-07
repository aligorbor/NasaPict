package ru.geekbrains.android2.nasapicture.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PictureOfTheDayAPI {
    @GET("planetary/apod")
    fun getPictureOfTheDay(
        @Query("date") strDate: String,
        @Query("api_key") apiKey: String
    ): Call<PODServerResponseData>
}