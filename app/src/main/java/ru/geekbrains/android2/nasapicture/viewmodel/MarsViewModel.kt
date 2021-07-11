package ru.geekbrains.android2.nasapicture.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.geekbrains.android2.nasapicture.BuildConfig
import ru.geekbrains.android2.nasapicture.model.MarsServerResponseDataList
import ru.geekbrains.android2.nasapicture.model.PODRetrofitImpl

class MarsViewModel(
    private val liveDataForViewToObserve: MutableLiveData<MarsData> = MutableLiveData(),
    private val retrofitImpl: PODRetrofitImpl = PODRetrofitImpl()
) : ViewModel() {
    fun getData(strDate: String): LiveData<MarsData> {
        sendServerRequest(strDate)
        return liveDataForViewToObserve
    }

    fun sendServerRequest(strDate: String) {
        liveDataForViewToObserve.value = MarsData.Loading(null)
        val apiKey: String = BuildConfig.NASA_API_KEY
        if (apiKey.isBlank()) {
            PictureOfTheDayData.Error(Throwable("You need API key"))
        } else {
            retrofitImpl.getRetrofitImpl().getMars(strDate, apiKey).enqueue(object :
                Callback<MarsServerResponseDataList> {
                override fun onResponse(
                    call: Call<MarsServerResponseDataList>,
                    response: Response<MarsServerResponseDataList>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        liveDataForViewToObserve.value =
                            MarsData.Success(response.body()!!)
                    } else {
                        val message = response.message()
                        if (message.isNullOrEmpty()) {
                            liveDataForViewToObserve.value =
                                MarsData.Error(Throwable("Unidentified error"))
                        } else {
                            liveDataForViewToObserve.value =
                                MarsData.Error(Throwable(message))
                        }
                    }
                }

                override fun onFailure(call: Call<MarsServerResponseDataList>, t: Throwable) {
                    liveDataForViewToObserve.value = MarsData.Error(t)
                }
            })
        }
    }
}