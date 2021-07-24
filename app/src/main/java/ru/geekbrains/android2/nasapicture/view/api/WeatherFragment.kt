package ru.geekbrains.android2.nasapicture.view.api

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_weather.*
import ru.geekbrains.android2.nasapicture.R
import ru.geekbrains.android2.nasapicture.model.DONKIServerResponseData
import ru.geekbrains.android2.nasapicture.util.dateSpan
import ru.geekbrains.android2.nasapicture.util.strDateBeforeNow
import ru.geekbrains.android2.nasapicture.util.themeColor
import ru.geekbrains.android2.nasapicture.util.toast
import ru.geekbrains.android2.nasapicture.viewmodel.DonkiData
import ru.geekbrains.android2.nasapicture.viewmodel.WeatherViewModel

private const val period = 30

class WeatherFragment : Fragment() {
    private val viewModel: WeatherViewModel by lazy {
        ViewModelProvider(this).get(WeatherViewModel::class.java)
    }
    private var arrWeather = mutableListOf<DONKIServerResponseData>()
    private var position = 0
    private var numberDaysFrom = period
    private var numberDaysTo = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getData(strDateBeforeNow(numberDaysFrom), strDateBeforeNow(numberDaysTo))
            .observe(viewLifecycleOwner, {
                renderData(it)
            })
        chip_next_weather.setOnClickListener {
            getUrl()
        }
        chip_date_prev_weather.setOnClickListener {
            numberDaysFrom += period
            numberDaysTo += period
            viewModel.sendServerRequest(
                strDateBeforeNow(numberDaysFrom),
                strDateBeforeNow(numberDaysTo)
            )
        }
        chip_date_next_weather.setOnClickListener {
            if (numberDaysTo > period) {
                numberDaysFrom -= period
                numberDaysTo -= period
                viewModel.sendServerRequest(
                    strDateBeforeNow(numberDaysFrom),
                    strDateBeforeNow(numberDaysTo)
                )
            }
        }
    }

    private fun renderData(data: DonkiData) {
        when (data) {
            is DonkiData.Success -> {
                data.serverResponseData?.let {
                    arrWeather.clear()
                    position = 0
                    image_view_weather.visibility = View.GONE
                    text_msg_weather.visibility = View.VISIBLE
                    arrWeather.addAll(it)
                    getUrl()
                    val str = "${strDateBeforeNow(numberDaysFrom)} - ${strDateBeforeNow(numberDaysTo)}"
                    val spannable = SpannableString(str)
                    spannable.setSpan(
                        ForegroundColorSpan(context?.themeColor(R.attr.colorPrimaryVariant) ?: Color.RED),
                        5, 7,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannable.setSpan(
                        ForegroundColorSpan(context?.themeColor(R.attr.colorPrimaryVariant) ?: Color.RED),
                        18, 20,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        5, 7,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        18, 20,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    chip_next_weather.text = spannable
                }
            }
            is DonkiData.Loading -> {
                image_view_weather.visibility = View.VISIBLE
                text_msg_weather.visibility = View.GONE
            }
            is DonkiData.Error -> {
                image_view_weather.visibility = View.VISIBLE
                text_msg_weather.visibility = View.GONE
                toast(data.error.message)
                Log.d("retrofit", data.error.message ?: "retrofit error")
            }
        }
    }

    private fun getUrl() {
        if (arrWeather.size > 0) {
            if (position > arrWeather.size - 1)
                position = 0
            arrWeather[position++].let {
                text_msg_weather.text = it.messageBody
                text_date_weather.text = it.messageIssueTime
            }
        }

    }

}
