package com.kompsia.sketchcolor.auth

import android.content.Context
import com.kompsia.sketchcolor.data.SketchDatabase
import com.kompsia.sketchcolor.network.ApiClient
import org.json.JSONObject

class AuthRepository(context: Context) {
    private val db = SketchDatabase(context.applicationContext)
    private val api = ApiClient()

    var token: String?
        get() = db.getState("auth_token")
        private set(value) {
            if (value != null) db.putState("auth_token", value)
        }

    fun register(email: String, password: String): String {
        val result = api.postJson("/auth/register", JSONObject().put("email", email).put("password", password))
        token = result.optString("token")
        return result.optString("message", "Registered")
    }

    fun login(email: String, password: String): String {
        val result = api.postJson("/auth/login", JSONObject().put("email", email).put("password", password))
        token = result.optString("token")
        return result.optString("message", "Logged in")
    }
}
