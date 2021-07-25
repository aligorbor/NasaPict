package ru.geekbrains.android2.nasapicture.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.geekbrains.android2.nasapicture.BuildConfig
import ru.geekbrains.android2.nasapicture.model.EPICServerResponseData
import ru.geekbrains.android2.nasapicture.model.PODRetrofitImpl

class EarthViewModel(
    private val liveDataForViewToObserve: MutableLiveData<EpicData> = MutableLiveData(),
    private val retrofitImpl: PODRetrofitImpl = PODRetrofitImpl()
) : ViewModel() {
    fun getData(): LiveData<EpicData> {
        sendServerRequest()
        return liveDataForViewToObserve
    }

    fun sendServerRequest() {
        liveDataForViewToObserve.value = EpicData.Loading(null)
        val apiKey: String = BuildConfig.NASA_API_KEY
        if (apiKey.isBlank()) {
            EpicData.Error(Throwable("You need API key"))
        } else {
            retrofitImpl.getRetrofitImpl().getEpic(apiKey).enqueue(object :
                Callback<List<EPICServerResponseData>> {
                override fun onResponse(
                    call: Call<List<EPICServerResponseData>>,
                    response: Response<List<EPICServerResponseData>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        liveDataForViewToObserve.value =
                            EpicData.Success(response.body()!!)
                    } else {
                        val message = response.message()
                        if (message.isNullOrEmpty()) {
                            liveDataForViewToObserve.value =
                                EpicData.Error(Throwable("Unidentified error"))
                        } else {
                            liveDataForViewToObserve.value =
                                EpicData.Error(Throwable(message))
                        }
                    }
                }

                override fun onFailure(call: Call<List<EPICServerResponseData>>, t: Throwable) {
                    liveDataForViewToObserve.value = EpicData.Error(t)
                }
            })
        }
    }

}