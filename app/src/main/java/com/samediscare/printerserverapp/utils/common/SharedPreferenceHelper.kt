package com.bong.brothersetup.utils.common

import android.content.Context
import android.content.SharedPreferences



object SharedPreferenceHelper {

    fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            Constants.PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    fun removeCurrentUser(context: Context) {
       // var userPreferences = UserPreferences(getPrefs(context))

    }
}