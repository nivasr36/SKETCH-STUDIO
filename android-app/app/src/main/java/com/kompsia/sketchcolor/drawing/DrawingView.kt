package com.kompsia.sketchcolor.drawing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.kompsia.sketchcolor.model.PointRecord
import com.kompsia.sketchcolor.model.StrokeRecord
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.max

class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var currentColor: Int = Color.BLACK
        set(value) {
            field = value
            isEraser = false
        }
    var brushSize: Float = 14f
    var isEraser: Boolean = false
    var onStrokeCommitted: (() -> Unit)? = null

    private val strokes = mutableListOf<StrokeRecord>()
    private val redoStack = mutableListOf<StrokeRecord>()
    private var activeStroke: StrokeRecord? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)
        strokes.forEach { drawStroke(canvas, it, width, height) }
        activeStroke?.let { drawStroke(canvas, it, width, height) }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val safeWidth = max(width, 1).toFloat()
        val safeHeight = max(height, 1).toFloat()
        val x = (event.x / safeWidth).coerceIn(0f, 1f)
        val y = (event.y / safeHeight).coerceIn(0f, 1f)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent?.requestDisallowInterceptTouchEvent(true)
                redoStack.clear()
                activeStroke = StrokeRecord(
                    color = if (isEraser) Color.WHITE else currentColor,
                    size = brushSize,
                    eraser = isEraser,
                    points = mutableListOf(PointRecord(x, y))
                )
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                activeStroke?.points?.add(PointRecord(x, y))
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                activeStroke?.let { stroke ->
                    stroke.points.add(PointRecord(x, y))
                    strokes.add(stroke)
                }
                activeStroke = null
                invalidate()
                onStrokeCommitted?.invoke()
                return true
            }
        }
        return true
    }

    fun undo() {
        if (strokes.isNotEmpty()) {
            redoStack.add(strokes.removeAt(strokes.lastIndex))
            invalidate()
            onStrokeCommitted?.invoke()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            strokes.add(redoStack.removeAt(redoStack.lastIndex))
            invalidate()
            onStrokeCommitted?.invoke()
        }
    }

    fun clearCanvas() {
        if (strokes.isNotEmpty()) {
            redoStack.clear()
            strokes.clear()
            invalidate()
            onStrokeCommitted?.invoke()
        }
    }

    fun toJson(): String {
        val root = JSONArray()
        strokes.forEach { stroke ->
            val item = JSONObject()
            item.put("color", stroke.color)
            item.put("size", stroke.size)
            item.put("eraser", stroke.eraser)
            val points = JSONArray()
            stroke.points.forEach { point ->
                val p = JSONObject()
                p.put("x", point.x)
                p.put("y", point.y)
                points.put(p)
            }
            item.put("points", points)
            root.put(item)
        }
        return root.toString()
    }

    fun loadJson(json: String) {
        strokes.clear()
        redoStack.clear()
        if (json.isBlank()) {
            invalidate()
            return
        }
        val root = JSONArray(json)
        for (i in 0 until root.length()) {
            val item = root.getJSONObject(i)
            val pointsArray = item.getJSONArray("points")
            val points = mutableListOf<PointRecord>()
            for (j in 0 until pointsArray.length()) {
                val p = pointsArray.getJSONObject(j)
                points.add(PointRecord(p.getDouble("x").toFloat(), p.getDouble("y").toFloat()))
            }
            strokes.add(
                StrokeRecord(
                    color = item.getInt("color"),
                    size = item.getDouble("size").toFloat(),
                    eraser = item.optBoolean("eraser", false),
                    points = points
                )
            )
        }
        invalidate()
    }

    fun renderToBitmap(targetWidth: Int, targetHeight: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        strokes.forEach { drawStroke(canvas, it, targetWidth, targetHeight) }
        return bitmap
    }

    private fun drawStroke(canvas: Canvas, stroke: StrokeRecord, canvasWidth: Int, canvasHeight: Int) {
        if (stroke.points.isEmpty()) return
        paint.color = if (stroke.eraser) Color.WHITE else stroke.color
        paint.strokeWidth = stroke.size * (canvasWidth / max(width, 1).toFloat()).coerceAtLeast(1f)
        val path = Path()
        val first = stroke.points.first()
        path.moveTo(first.x * canvasWidth, first.y * canvasHeight)
        for (i in 1 until stroke.points.size) {
            val p = stroke.points[i]
            path.lineTo(p.x * canvasWidth, p.y * canvasHeight)
        }
        canvas.drawPath(path, paint)
    }
}
