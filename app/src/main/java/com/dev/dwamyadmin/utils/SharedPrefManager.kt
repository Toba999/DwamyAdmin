package com.dev.dwamyadmin.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.dev.dwamyadmin.utils.FireStoreConstant.KEY_ADMIN_EMAIL
import com.dev.dwamyadmin.utils.FireStoreConstant.KEY_ADMIN_ID
import com.dev.dwamyadmin.utils.FireStoreConstant.KEY_ADMIN_NAME
import com.dev.dwamyadmin.utils.FireStoreConstant.PREFS_NAME
import javax.inject.Inject


class SharedPrefManager @Inject constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setAdminData(id: String, name: String, email: String) {
        sharedPreferences.edit().apply {
            putString(KEY_ADMIN_ID, id)
            putString(KEY_ADMIN_NAME, name)
            putString(KEY_ADMIN_EMAIL, email)
            apply()
        }
    }

    fun getAdminId(): String? = sharedPreferences.getString(KEY_ADMIN_ID, null)
    fun getAdminName(): String? = sharedPreferences.getString(KEY_ADMIN_NAME, null)
    fun getAdminEmail(): String? = sharedPreferences.getString(KEY_ADMIN_EMAIL, null)

    fun clearAdminData() {
        sharedPreferences.edit { clear() }
    }
}