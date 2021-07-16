package ru.geekbrains.android2.nasapicture.view.api

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import kotlinx.android.synthetic.main.activity_api_b.*
import ru.geekbrains.android2.nasapicture.R
import ru.geekbrains.android2.nasapicture.util.ZoomOutPageTransformer
import ru.geekbrains.android2.nasapicture.util.configChanged
import ru.geekbrains.android2.nasapicture.util.initTheme
import ru.geekbrains.android2.nasapicture.util.themeColor

private const val EARTH = 0
private const val MARS = 1
private const val WEATHER = 2

class ApiActivity : AppCompatActivity(), OnOffsetChangedListener {
    private var mIsAvatarShown = true
    private var mMaxScrollSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme(this@ApiActivity)
        super.onCreate(savedInstanceState)
        //     setContentView(R.layout.activity_api)
        setContentView(R.layout.activity_api_b)

        setSupportActionBar(findViewById(R.id.toolbar))
        toolbar_layout.title = title
        toolbar.setNavigationOnClickListener { onBackPressed() }
        app_bar.addOnOffsetChangedListener(this)
        mMaxScrollSize = app_bar.totalScrollRange

        view_pager.adapter = ViewPagerAdapter(supportFragmentManager)
        view_pager.setPageTransformer(true, ZoomOutPageTransformer())
        tab_layout.setupWithViewPager(view_pager)
        setHighlightedTab(EARTH)

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                setHighlightedTab(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        configChanged(this@ApiActivity, newConfig)
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setHighlightedTab(position: Int) {
        val layoutInflater = LayoutInflater.from(this@ApiActivity)

        tab_layout.getTabAt(EARTH)?.customView = null
        tab_layout.getTabAt(MARS)?.customView = null
        tab_layout.getTabAt(WEATHER)?.customView = null

        when (position) {
            EARTH -> {
                profile_image.setImageResource(R.drawable.bg_earth)
                title_appbar.text = getString(R.string.titleEarth)
                subtitle_appbar.text = getString(R.string.subtitleEarth)
                setEarthTabHighlighted(layoutInflater)
            }
            MARS -> {
                title_appbar.text = getString(R.string.titleMars)
                subtitle_appbar.text = getString(R.string.subtitleMars)
                profile_image.setImageResource(R.drawable.bg_mars)
                setMarsTabHighlighted(layoutInflater)
            }
            WEATHER -> {
                title_appbar.text = getString(R.string.titleWeather)
                subtitle_appbar.text = getString(R.string.subtitleWeather)
                profile_image.setImageResource(R.drawable.bg_system)
                setWeatherTabHighlighted(layoutInflater)
            }
            else -> {
                setEarthTabHighlighted(layoutInflater)
            }
        }
    }

    private fun setEarthTabHighlighted(layoutInflater: LayoutInflater) {
        val earth =
            layoutInflater.inflate(R.layout.activity_api_custom_tab_earth, null)
        earth.findViewById<AppCompatTextView>(R.id.tab_image_textview)
            .setTextColor(this@ApiActivity.themeColor(R.attr.colorAccent))
        tab_layout.getTabAt(EARTH)?.customView = earth
        tab_layout.getTabAt(MARS)?.customView =
            layoutInflater.inflate(R.layout.activity_api_custom_tab_mars, null)
        tab_layout.getTabAt(WEATHER)?.customView =
            layoutInflater.inflate(R.layout.activity_api_custom_tab_weather, null)
    }

    private fun setMarsTabHighlighted(layoutInflater: LayoutInflater) {
        val mars =
            layoutInflater.inflate(R.layout.activity_api_custom_tab_mars, null)
        mars.findViewById<AppCompatTextView>(R.id.tab_image_textview)
            .setTextColor(this@ApiActivity.themeColor(R.attr.colorAccent))
        tab_layout.getTabAt(EARTH)?.customView =
            layoutInflater.inflate(R.layout.activity_api_custom_tab_earth, null)
        tab_layout.getTabAt(MARS)?.customView = mars
        tab_layout.getTabAt(WEATHER)?.customView =
            layoutInflater.inflate(R.layout.activity_api_custom_tab_weather, null)
    }

    private fun setWeatherTabHighlighted(layoutInflater: LayoutInflater) {
        val weather =
            layoutInflater.inflate(R.layout.activity_api_custom_tab_weather, null)
        weather.findViewById<AppCompatTextView>(R.id.tab_image_textview)
            .setTextColor(this@ApiActivity.themeColor(R.attr.colorAccent))
        tab_layout.getTabAt(EARTH)?.customView =
            layoutInflater.inflate(R.layout.activity_api_custom_tab_earth, null)
        tab_layout.getTabAt(MARS)?.customView =
            layoutInflater.inflate(R.layout.activity_api_custom_tab_mars, null)
        tab_layout.getTabAt(WEATHER)?.customView = weather
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if (mMaxScrollSize == 0) mMaxScrollSize = appBarLayout!!.totalScrollRange

        val percentage: Int = Math.abs(verticalOffset) * 100 / mMaxScrollSize

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false
            profile_image.animate()
                .scaleY(0f).scaleX(0f)
                .setDuration(200)
                .start()
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true
            profile_image.animate()
                .scaleY(1f).scaleX(1f)
                .start()
        }
    }

    companion object {
        const val PERCENTAGE_TO_ANIMATE_AVATAR = 20
    }

}
