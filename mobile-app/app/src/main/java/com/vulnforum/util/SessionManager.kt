package com.vulnforum.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit


class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun saveSession(token: String, username: String, role: String, balance: Float) {
        prefs.edit().apply {
            putString("jwt_token", token)
            putString("username", username)
            putString("role", role)
            putFloat("balance",balance)
            apply()
        }
    }

    fun getToken(): String? = prefs.getString("jwt_token", null)
    fun getUsername(): String? = prefs.getString("username", null)
    fun getRole(): String? = prefs.getString("role", null)
    fun getBalance(): Float? = prefs.getFloat("balance", 0f)

    fun clear() {
        prefs.edit() { clear() }
    }
}
