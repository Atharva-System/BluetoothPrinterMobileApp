package com.bong.brothersetup.utils.common

import android.content.Context
import android.content.SharedPreferences



class UserPreferences private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences = context.applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    companion object {
        private var instance: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences {
            if (instance == null) {
                instance = UserPreferences(context)
            }
            return instance as UserPreferences
        }
    }

    // Additional methods for accessing and modifying shared preferences
    fun saveData(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getData(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
    fun saveInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }
    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue) ?: defaultValue
    }
    // Add more methods as per your requirements
}