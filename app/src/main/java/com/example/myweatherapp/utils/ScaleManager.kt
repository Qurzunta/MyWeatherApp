package com.example.myweatherapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.myweatherapp.utils.Constants.KEY_SCALETYPE
import com.example.myweatherapp.utils.Constants.PREFS_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ScaleManager @Inject constructor(@ApplicationContext context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveScale(scaletype: String) {
        val editor = prefs.edit()
        editor.putString(KEY_SCALETYPE, scaletype)
        editor.apply()
    }

    fun getScale(): String? {
        return prefs.getString(KEY_SCALETYPE, null)
    }
}