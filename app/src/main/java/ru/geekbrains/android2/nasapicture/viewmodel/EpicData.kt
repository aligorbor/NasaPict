package ru.geekbrains.android2.nasapicture.viewmodel

import ru.geekbrains.android2.nasapicture.model.EPICServerResponseData

sealed class EpicData {
    data class Success(val serverResponseData: List<EPICServerResponseData>) : EpicData()
    data class Error(val error: Throwable) : EpicData()
    data class Loading(val progress: Int?) : EpicData()
}
