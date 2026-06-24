package com.kompsia.sketchcolor.analytics

import android.content.Context
import com.kompsia.sketchcolor.data.SketchDatabase
import org.json.JSONObject

class AnalyticsManager(context: Context) {
    private val db = SketchDatabase(context.applicationContext)

    fun track(eventName: String, payload: JSONObject = JSONObject()) {
        db.logEvent(eventName, payload.toString())
    }
}
