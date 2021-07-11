package ru.geekbrains.android2.nasapicture

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import ru.geekbrains.android2.nasapicture.util.configChanged
import ru.geekbrains.android2.nasapicture.util.initTheme
import ru.geekbrains.android2.nasapicture.view.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme(this@MainActivity)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        configChanged(this@MainActivity,newConfig)

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