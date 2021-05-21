package com.example.pxandroidexample.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.pxandroidexample.R

class SessionManager(context : Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.app_name), Context.MODE_PRIVATE)

    fun saveLastPrefId(prefId : String){
        val editor = prefs.edit()
        editor.putString(LAST_PREF_KEY, prefId)
        editor.apply()
    }

    fun getLastPrefId() : String?{
        return prefs.getString(LAST_PREF_KEY, null)
    }

    companion object{
        const val LAST_PREF_KEY = "LAST_PREF_ID"
    }
}