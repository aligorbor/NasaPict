package ru.geekbrains.android2.nasapicture.util

import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import ru.geekbrains.android2.nasapicture.MainActivity
import ru.geekbrains.android2.nasapicture.R
import java.text.SimpleDateFormat
import java.util.*

fun strDateBeforeNow(numberDays: Int): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val c = Calendar.getInstance()
    c.time = Date()
    c.add(Calendar.DATE, -numberDays)
    return sdf.format(c.time)
}

fun Context.themeColor(@AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrRes, typedValue, true)
    return typedValue.data
}

fun initTheme(context: Context) {
    context.apply {
        val currentTheme = getSharedPreferences(MainActivity.preferencesName, Context.MODE_PRIVATE)
            .getInt(MainActivity.themeKey, R.style.Theme_NasaPicture_Earth)
        setTheme(currentTheme)
        val currentDark = getSharedPreferences(MainActivity.preferencesName, Context.MODE_PRIVATE)
            .getBoolean(MainActivity.darkKey, false)
        AppCompatDelegate.setDefaultNightMode(
            if (currentDark)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}

fun configChanged(context: Context, newConfig: Configuration) {
    context.apply {
        val preferences =
            getSharedPreferences(MainActivity.preferencesName, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val currNightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                editor.putBoolean(MainActivity.darkKey, false)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                editor.putBoolean(MainActivity.darkKey, true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
        editor.apply()
    }
}

fun Fragment.toast(string: String?) {
    Toast.makeText(context, string, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.BOTTOM, 0, 250)
        show()
    }
}
