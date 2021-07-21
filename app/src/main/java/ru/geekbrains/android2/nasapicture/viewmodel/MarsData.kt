package ru.geekbrains.android2.nasapicture.viewmodel

import ru.geekbrains.android2.nasapicture.model.MarsServerResponseDataList

sealed class MarsData {
    data class Success(val serverResponseData: MarsServerResponseDataList) : MarsData()
    data class Error(val error: Throwable) : MarsData()
    data class Loading(val progress: Int?) : MarsData()
}