package ru.geekbrains.android2.nasapicture.view.animations

import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import kotlinx.android.synthetic.main.activity_animations.*
import kotlinx.android.synthetic.main.activity_animations_enlarge.*
import kotlinx.android.synthetic.main.activity_animations_explode.*
import kotlinx.android.synthetic.main.activity_animations_path_transitions.*
import kotlinx.android.synthetic.main.activity_animations_shuffle.*
import ru.geekbrains.android2.nasapicture.R

private const val maxCont = 4

class AnimationsActivity : AppCompatActivity() {
    private var textIsVisible = false
    private var isExpanded = false
    private var contrs = 1
    private var toRightAnimation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animations)
        button_animate.setOnClickListener {
            recycler_view.adapter = Adapter()
            TransitionManager.beginDelayedTransition(transitions_container, Slide(Gravity.END))
            textIsVisible = !textIsVisible
            text_animate.visibility = if (textIsVisible) View.VISIBLE else View.GONE
            when (contrs) {
                1 -> {
                    recycler_view.visibility = View.VISIBLE
                    container_enlarge.visibility = View.GONE
                    container_path.visibility = View.GONE
                    container_shuff.visibility = View.GONE
                }
                2 -> {
                    recycler_view.visibility = View.GONE
                    container_enlarge.visibility = View.VISIBLE
                    container_path.visibility = View.GONE
                    container_shuff.visibility = View.GONE
                }
                3 -> {
                    recycler_view.visibility = View.GONE
                    container_enlarge.visibility = View.GONE
                    container_path.visibility = View.VISIBLE
                    container_shuff.visibility = View.GONE
                }
                4 -> {
                    recycler_view.visibility = View.GONE
                    container_enlarge.visibility = View.GONE
                    container_path.visibility = View.GONE
                    container_shuff.visibility = View.VISIBLE
                }
            }
            if (++contrs > maxCont) contrs = 1
        }
        image_view_enlarge.setOnClickListener {
            isExpanded = !isExpanded
            TransitionManager.beginDelayedTransition(
                container_enlarge, TransitionSet()
                    .addTransition(ChangeBounds())
                    .addTransition(ChangeImageTransform())
            )
            val params: ViewGroup.LayoutParams = image_view_enlarge.layoutParams
            params.height = if (isExpanded) ViewGroup.LayoutParams.MATCH_PARENT else
                ViewGroup.LayoutParams.WRAP_CONTENT
            image_view_enlarge.layoutParams = params
            image_view_enlarge.scaleType = if (isExpanded) ImageView.ScaleType.CENTER_CROP else
                ImageView.ScaleType.FIT_CENTER
        }

        button_path.setOnClickListener {
            val changeBounds = ChangeBounds()
            changeBounds.setPathMotion(ArcMotion())
            changeBounds.duration = 1000
            TransitionManager.beginDelayedTransition(
                container_path,
                changeBounds
            )
            toRightAnimation = !toRightAnimation
            val params = button_path.layoutParams as FrameLayout.LayoutParams
            params.gravity =
                if (toRightAnimation) Gravity.END or Gravity.BOTTOM else
                    Gravity.START or Gravity.TOP
            button_path.layoutParams = params
        }

        val titles: MutableList<String> = ArrayList()
        for (i in 0..30) {
            titles.add(String.format("Item %d", i + 1))
        }
        createViews(transitions_container_shuff, titles)
        button_shuff.setOnClickListener {
            TransitionManager.beginDelayedTransition(
                transitions_container_shuff,
                ChangeBounds()
            )
            titles.shuffle()
            createViews(transitions_container_shuff, titles)
        }

    }

    inner class Adapter : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.activity_animations_explode_recycle_view_item,
                    parent,
                    false
                ) as View
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemView.setOnClickListener {
                explode(it)
            }
        }

        override fun getItemCount(): Int {
            return 32
        }
    }

    private fun explode(clickedView: View) {
        val viewRect = Rect()
        clickedView.getGlobalVisibleRect(viewRect)
        val explode = Explode()
        explode.epicenterCallback = object : Transition.EpicenterCallback() {
            override fun onGetEpicenter(transition: Transition): Rect {
                return viewRect
            }
        }
        explode.duration = 2000
        explode.excludeTarget(clickedView, true)
        val set = TransitionSet()
            .addTransition(explode)
            .addTransition(Fade().addTarget(clickedView))
            .addListener(object : TransitionListenerAdapter() {
                override fun onTransitionEnd(transition: Transition) {
                    transition.removeListener(this)
                    onBackPressed()
                    super.onTransitionEnd(transition)
                }
            })
        set.duration = 2000
        TransitionManager.beginDelayedTransition(recycler_view, set)
        recycler_view.adapter = null
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private fun createViews(layout: ViewGroup, titles: List<String>) {
        layout.removeAllViews()
        for (title in titles) {
            val textView = TextView(this)
            textView.text = title
            textView.gravity = Gravity.CENTER_HORIZONTAL
            ViewCompat.setTransitionName(textView, title)
            layout.addView(textView)
        }
    }
}