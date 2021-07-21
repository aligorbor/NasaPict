package ru.geekbrains.android2.nasapicture.model

import com.google.gson.annotations.SerializedName

data class EPICServerResponseData(
    @field:SerializedName("identifier") val identifier: String?,
    @field:SerializedName("caption") val caption: String?,
    @field:SerializedName("image") val image: String?,
    @field:SerializedName("date") val date: String?
)