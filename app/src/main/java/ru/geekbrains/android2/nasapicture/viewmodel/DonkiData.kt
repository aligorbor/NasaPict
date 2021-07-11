package ru.geekbrains.android2.nasapicture.viewmodel

import ru.geekbrains.android2.nasapicture.model.DONKIServerResponseData

sealed class DonkiData {
    data class Success(val serverResponseData: List<DONKIServerResponseData>) : DonkiData()
    data class Error(val error: Throwable) : DonkiData()
    data class Loading(val progress: Int?) : DonkiData()
}
