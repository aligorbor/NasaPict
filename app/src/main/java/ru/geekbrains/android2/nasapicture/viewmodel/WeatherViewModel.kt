package ru.geekbrains.android2.nasapicture.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.geekbrains.android2.nasapicture.BuildConfig
import ru.geekbrains.android2.nasapicture.model.DONKIServerResponseData
import ru.geekbrains.android2.nasapicture.model.PODRetrofitImpl

class WeatherViewModel(
    private val liveDataForViewToObserve: MutableLiveData<DonkiData> = MutableLiveData(),
    private val retrofitImpl: PODRetrofitImpl = PODRetrofitImpl()
) : ViewModel() {
    fun getData(startDate: String, endDate: String): LiveData<DonkiData> {
        sendServerRequest(startDate, endDate)
        return liveDataForViewToObserve
    }

    fun sendServerRequest(startDate: String, endDate: String) {
        liveDataForViewToObserve.value = DonkiData.Loading(null)
        val apiKey: String = BuildConfig.NASA_API_KEY
        if (apiKey.isBlank()) {
            EpicData.Error(Throwable("You need API key"))
        } else {
            retrofitImpl.getRetrofitImpl().getDonki(startDate, endDate, "all", apiKey)
                .enqueue(object :
                    Callback<List<DONKIServerResponseData>> {
                    override fun onResponse(
                        call: Call<List<DONKIServerResponseData>>,
                        response: Response<List<DONKIServerResponseData>>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            liveDataForViewToObserve.value =
                                DonkiData.Success(response.body()!!)
                        } else {
                            val message = response.message()
                            if (message.isNullOrEmpty()) {
                                liveDataForViewToObserve.value =
                                    DonkiData.Error(Throwable("Unidentified error"))
                            } else {
                                liveDataForViewToObserve.value =
                                    DonkiData.Error(Throwable(message))
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<List<DONKIServerResponseData>>,
                        t: Throwable
                    ) {
                        liveDataForViewToObserve.value = DonkiData.Error(t)
                    }
                })
        }
    }

}