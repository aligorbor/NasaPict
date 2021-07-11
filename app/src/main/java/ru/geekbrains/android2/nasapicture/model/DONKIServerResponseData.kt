package ru.geekbrains.android2.nasapicture.model

import com.google.gson.annotations.SerializedName

data class DONKIServerResponseData(
    @field:SerializedName("messageType") val messageType: String?,
    @field:SerializedName("messageID") val messageID: String?,
    @field:SerializedName("messageURL") val messageURL: String?,
    @field:SerializedName("messageIssueTime") val messageIssueTime: String?,
    @field:SerializedName("messageBody") val messageBody: String?
)
