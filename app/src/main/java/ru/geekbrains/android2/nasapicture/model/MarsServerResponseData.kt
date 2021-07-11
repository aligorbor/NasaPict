package ru.geekbrains.android2.nasapicture.model

import com.google.gson.annotations.SerializedName

data class MarsServerResponseData(
    @field:SerializedName("id") val id: Int?,
    @field:SerializedName("img_src") val img_src: String?,
    @field:SerializedName("earth_date") val earth_date: String?
)