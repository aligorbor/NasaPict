package ru.geekbrains.android2.nasapicture.view.api

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.*
import coil.api.load
import kotlinx.android.synthetic.main.bottom_sheet_layout.*
import kotlinx.android.synthetic.main.fragment_earth.*
import ru.geekbrains.android2.nasapicture.R
import ru.geekbrains.android2.nasapicture.model.EPICServerResponseData
import ru.geekbrains.android2.nasapicture.util.toast
import ru.geekbrains.android2.nasapicture.viewmodel.EarthViewModel
import ru.geekbrains.android2.nasapicture.viewmodel.EpicData


private const val baseImageUrl = "https://epic.gsfc.nasa.gov/archive/natural/"
private const val imageType = "png"

class EarthFragment : Fragment() {
    private val viewModel: EarthViewModel by lazy {
        ViewModelProvider(this).get(EarthViewModel::class.java)
    }
    private var arrEpic = mutableListOf<EPICServerResponseData>()
    private var position = 0
    private var isExpanded = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_earth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getData().observe(viewLifecycleOwner, {
            renderData(it)
        })
        chip_next_earth.setOnClickListener {
            getImage()
        }
        image_view_earth.setOnClickListener {
            isExpanded = !isExpanded
            TransitionManager.beginDelayedTransition(
                container_earth, TransitionSet()
                    .addTransition(ChangeBounds())
                    .addTransition(ChangeImageTransform())
            )
            val params: ViewGroup.LayoutParams = image_view_earth.layoutParams
            params.height = if (isExpanded) ViewGroup.LayoutParams.MATCH_PARENT else
                ViewGroup.LayoutParams.WRAP_CONTENT
            image_view_earth.layoutParams = params
            image_view_earth.scaleType = if (isExpanded) ImageView.ScaleType.CENTER_CROP else
                ImageView.ScaleType.FIT_CENTER
        }
    }

    private fun renderData(data: EpicData) {
        when (data) {
            is EpicData.Success -> {
                arrEpic.addAll(data.serverResponseData)
                getImage()
            }
            is EpicData.Loading -> {
            }
            is EpicData.Error -> {
                toast(data.error.message)
                Log.d("retrofit", data.error.message ?: "retrofit error")
            }
        }
    }

    private fun getImage() {
        image_view_earth.visibility = View.GONE
        getUrl()
        TransitionManager.beginDelayedTransition(container_earth, Slide(Gravity.END))
        image_view_earth.visibility = View.VISIBLE
    }

    private fun getUrl() {
        if (arrEpic.size > 0) {
            if (position > arrEpic.size - 1)
                position = 0
            arrEpic[position].let {
                val dt = it.date?.substringBefore(' ')
                position++
                val url = "$baseImageUrl${dt?.replace('-', '/')}/$imageType/${it.image}.$imageType"
                if (url.isNullOrEmpty()) {
                    toast("Link is empty")
                } else {
                    image_view_earth.load(url) {
                        lifecycle(this@EarthFragment)
                        error(R.drawable.ic_load_error_vector)
                        placeholder(R.drawable.bg_earth)
                    }
                }
            }
        }
    }

}
