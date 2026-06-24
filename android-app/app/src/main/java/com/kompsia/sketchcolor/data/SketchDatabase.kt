package com.kompsia.sketchcolor.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SketchDatabase(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE artworks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                strokes_json TEXT NOT NULL,
                canvas_width INTEGER NOT NULL,
                canvas_height INTEGER NOT NULL,
                updated_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE analytics_events (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                event_name TEXT NOT NULL,
                payload TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE app_state (
                key TEXT PRIMARY KEY,
                value TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS artworks")
        db.execSQL("DROP TABLE IF EXISTS analytics_events")
        db.execSQL("DROP TABLE IF EXISTS app_state")
        onCreate(db)
    }

    fun saveLatestDraft(title: String, strokesJson: String, width: Int, height: Int): Long {
        val latest = loadLatestArtwork()
        val values = ContentValues().apply {
            put("title", title)
            put("strokes_json", strokesJson)
            put("canvas_width", width)
            put("canvas_height", height)
            put("updated_at", System.currentTimeMillis())
        }
        return if (latest == null) {
            writableDatabase.insert("artworks", null, values)
        } else {
            writableDatabase.update("artworks", values, "id=?", arrayOf(latest.id.toString()))
            latest.id
        }
    }

    fun loadLatestArtwork(): ArtworkRecord? {
        val cursor = readableDatabase.query(
            "artworks",
            arrayOf("id", "title", "strokes_json", "canvas_width", "canvas_height", "updated_at"),
            null,
            null,
            null,
            null,
            "updated_at DESC",
            "1"
        )
        cursor.use {
            if (!it.moveToFirst()) return null
            return ArtworkRecord(
                id = it.getLong(0),
                title = it.getString(1),
                strokesJson = it.getString(2),
                canvasWidth = it.getInt(3),
                canvasHeight = it.getInt(4),
                updatedAt = it.getLong(5)
            )
        }
    }

    fun logEvent(name: String, payload: String = "{}") {
        val values = ContentValues().apply {
            put("event_name", name)
            put("payload", payload)
            put("created_at", System.currentTimeMillis())
        }
        writableDatabase.insert("analytics_events", null, values)
    }

    fun putState(key: String, value: String) {
        val values = ContentValues().apply {
            put("key", key)
            put("value", value)
        }
        writableDatabase.insertWithOnConflict("app_state", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getState(key: String): String? {
        val cursor = readableDatabase.query("app_state", arrayOf("value"), "key=?", arrayOf(key), null, null, null)
        cursor.use {
            if (!it.moveToFirst()) return null
            return it.getString(0)
        }
    }

    companion object {
        private const val DB_NAME = "sketchcolor_studio.db"
        private const val DB_VERSION = 1
    }
}
