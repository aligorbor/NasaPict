package ru.geekbrains.android2.nasapicture.model

import com.google.gson.annotations.SerializedName

data class MarsServerResponseDataList(
    @field:SerializedName("photos")
    val photos: List<MarsServerResponseData>?
)
