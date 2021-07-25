package ru.geekbrains.android2.nasapicture.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.settings_fragment.*
import ru.geekbrains.android2.nasapicture.MainActivity
import ru.geekbrains.android2.nasapicture.R

class SettingsFragment : Fragment(R.layout.settings_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewSettings()
        chipGroupTheme.setOnCheckedChangeListener { chipGroup, position ->
            chipGroupTheme.findViewById<Chip>(position)?.let {
                if (it.isChecked)
                    savePreferences(chipIdToThemeId(it.id), chip_dark.isChecked)
                Toast.makeText(context, "Выбран ${it.text}", Toast.LENGTH_SHORT).show()
            }
        }

        chip_dark.setOnCheckedChangeListener { buttonView, isChecked ->
            savePreferences(chipIdToThemeId(chipGroupTheme.checkedChipId), chip_dark.isChecked)
            if (isChecked) {
                Toast.makeText(
                    context,
                    "Dark is choosed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onPause() {
        activity?.recreate()
        super.onPause()
    }

    private fun initViewSettings() {
        activity?.let { fragmentActivity ->
            chip_dark.isChecked = fragmentActivity.getSharedPreferences(
                MainActivity.preferencesName,
                Context.MODE_PRIVATE
            )
                .getBoolean(MainActivity.darkKey, false)
            chipGroupTheme.children.toList().forEach {
                (it as Chip).isChecked = (it.id == themeIdToChipId(
                    fragmentActivity.getSharedPreferences(
                        MainActivity.preferencesName,
                        Context.MODE_PRIVATE
                    )
                        .getInt(MainActivity.themeKey, 0)
                ))
            }
        }

    }

    private fun chipIdToThemeId(chipId: Int): Int {
        when (chipId) {
            R.id.chipThemeDefault -> return R.style.Theme_NasaPicture_Default
            R.id.chipThemeMars -> return R.style.Theme_NasaPicture_Mars
            R.id.chipThemeMoon -> return R.style.Theme_NasaPicture_Moon
            R.id.chipThemeEarth -> return R.style.Theme_NasaPicture_Earth
        }
        return R.style.Theme_NasaPicture
    }

    private fun themeIdToChipId(themeId: Int): Int {
        when (themeId) {
            R.style.Theme_NasaPicture_Default -> return R.id.chipThemeDefault
            R.style.Theme_NasaPicture_Mars -> return R.id.chipThemeMars
            R.style.Theme_NasaPicture_Moon -> return R.id.chipThemeMoon
            R.style.Theme_NasaPicture_Earth -> return R.id.chipThemeEarth
        }
        return R.id.chipThemeDefault
    }

    private fun savePreferences(rTheme: Int, bDark: Boolean) {
        activity?.let {
            val preferences =
                it.getSharedPreferences(MainActivity.preferencesName, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putInt(MainActivity.themeKey, rTheme)
            editor.putBoolean(MainActivity.darkKey, bDark)
            editor.apply()
        }
    }

}