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

    @GET("EPIC/api/natural")
    fun getEpic(
        @Query("api_key") apiKey: String
    ): Call<List<EPICServerResponseData>>

    @GET("mars-photos/api/v1/rovers/perseverance/photos")
    fun getMars(
        @Query("earth_date") strDate: String,
        @Query("api_key") apiKey: String
    ): Call<MarsServerResponseDataList>

    @GET("DONKI/notifications")
    fun getDonki(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("type") type: String,
        @Query("api_key") apiKey: String
    ): Call<List<DONKIServerResponseData>>
}