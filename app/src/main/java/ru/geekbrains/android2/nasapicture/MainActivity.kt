package ru.geekbrains.android2.nasapicture

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import ru.geekbrains.android2.nasapicture.view.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

    fun initTheme() {
        val currentTheme = getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
            .getInt(themeKey, R.style.Theme_NasaPicture_Earth)
        setTheme(currentTheme)
        val currentDark = getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
            .getBoolean(darkKey, false)
        AppCompatDelegate.setDefaultNightMode(
            if (currentDark)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        val preferences =
            getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val currNightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                editor.putBoolean(darkKey, false)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                editor.putBoolean(darkKey, true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
        editor.apply()

        Toast.makeText(
            this,
            "ConfigurationChanged",
            Toast.LENGTH_SHORT
        ).show()
        super.onConfigurationChanged(newConfig)
    }

    companion object {
        const val themeKey = "ThemeKey"
        const val darkKey = "DarkKey"
        const val preferencesName = "MainPreferences"
    }
}