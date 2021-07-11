package ru.geekbrains.android2.nasapicture.view.api

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import kotlinx.android.synthetic.main.fragment_mars.*
import ru.geekbrains.android2.nasapicture.R
import ru.geekbrains.android2.nasapicture.model.MarsServerResponseData
import ru.geekbrains.android2.nasapicture.util.strDateBeforeNow
import ru.geekbrains.android2.nasapicture.util.toast
import ru.geekbrains.android2.nasapicture.viewmodel.MarsData
import ru.geekbrains.android2.nasapicture.viewmodel.MarsViewModel

class MarsFragment : Fragment() {
    private val viewModel: MarsViewModel by lazy {
        ViewModelProvider(this).get(MarsViewModel::class.java)
    }
    private var arrMars = mutableListOf<MarsServerResponseData>()
    private var position = 0
    private var numberDays = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mars, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getData(strDateBeforeNow(numberDays)).observe(viewLifecycleOwner, {
            renderData(it)
        })
        chip_next_mars.setOnClickListener {
            getUrl()
        }
        chip_date_prev_mars.setOnClickListener {
            viewModel.sendServerRequest(strDateBeforeNow(++numberDays))
        }
        chip_date_next_mars.setOnClickListener {
            if (numberDays > 1) {
                viewModel.sendServerRequest(strDateBeforeNow(--numberDays))
            }
        }
    }

    private fun renderData(data: MarsData) {
        when (data) {
            is MarsData.Success -> {
                data.serverResponseData.photos?.let {
                    arrMars.clear()
                    position = 0
                    image_view_mars.setImageResource(R.drawable.bg_mars)
                    arrMars.addAll(it)
                    getUrl()
                    chip_next_mars.text = strDateBeforeNow(numberDays)
                }
            }
            is MarsData.Loading -> {
            }
            is MarsData.Error -> {
                toast(data.error.message)
                Log.d("retrofit", data.error.message ?: "retrofit error")
            }
        }
    }

    private fun getUrl() {
        if (arrMars.size > 0) {
            if (position > arrMars.size - 1)
                position = 0
            arrMars[position++].let {
                val url = it.img_src
                if (url.isNullOrEmpty()) {
                    toast("Link is empty")
                } else {
                    image_view_mars.load(url) {
                        lifecycle(this@MarsFragment)
                        error(R.drawable.ic_load_error_vector)
                        placeholder(R.drawable.bg_mars)
                    }
                }
            }
        }

    }


}