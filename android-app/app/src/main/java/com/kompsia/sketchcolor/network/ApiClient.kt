package com.kompsia.sketchcolor.network

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ApiClient {
    // For emulator talking to local backend: http://10.0.2.2:8080
    // For real device: use your computer/VPS IP or deployed backend URL.
    var baseUrl: String = "http://10.0.2.2:8080"

    fun postJson(path: String, body: JSONObject, token: String? = null): JSONObject {
        val url = URL(baseUrl.trimEnd('/') + path)
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10000
            readTimeout = 10000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            token?.let { setRequestProperty("Authorization", "Bearer $it") }
        }
        OutputStreamWriter(connection.outputStream).use { it.write(body.toString()) }
        val responseCode = connection.responseCode
        val stream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
        val text = BufferedReader(InputStreamReader(stream)).use { it.readText() }
        val json = if (text.isBlank()) JSONObject() else JSONObject(text)
        if (responseCode !in 200..299) {
            throw IllegalStateException(json.optString("error", "Server error $responseCode"))
        }
        return json
    }
}
