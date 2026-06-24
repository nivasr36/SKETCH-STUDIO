package com.kompsia.sketchcolor

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.kompsia.sketchcolor.ads.AdsManager
import com.kompsia.sketchcolor.analytics.AnalyticsManager
import com.kompsia.sketchcolor.auth.AuthRepository
import com.kompsia.sketchcolor.billing.BillingManager
import com.kompsia.sketchcolor.data.SketchDatabase
import com.kompsia.sketchcolor.drawing.DrawingView
import com.kompsia.sketchcolor.export.GalleryExporter
import com.kompsia.sketchcolor.notifications.NotificationHelper
import org.json.JSONObject
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var drawingView: DrawingView
    private lateinit var database: SketchDatabase
    private lateinit var analytics: AnalyticsManager
    private lateinit var auth: AuthRepository
    private lateinit var billing: BillingManager
    private var adsContainer: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = SketchDatabase(this)
        analytics = AnalyticsManager(this)
        auth = AuthRepository(this)
        NotificationHelper.createChannel(this)
        requestNotificationPermissionIfNeeded()
        AdsManager.initialize(this)
        billing = BillingManager(this) { unlockPremiumLocally() }
        billing.start()
        buildUi()
        loadLatestDraft()
        analytics.track("app_open")
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(21, 34, 48))
        }

        val title = TextView(this).apply {
            text = "SketchColor Studio"
            setTextColor(Color.WHITE)
            textSize = 20f
            gravity = Gravity.CENTER
            setPadding(12, 16, 12, 10)
        }
        root.addView(title, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        val toolbar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(8, 4, 8, 4)
        }
        root.addView(toolbar, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        drawingView = DrawingView(this).apply {
            setBackgroundColor(Color.WHITE)
            onStrokeCommitted = { autosaveDraft() }
        }
        root.addView(drawingView, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))

        val tools = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(8, 6, 8, 6)
        }
        root.addView(tools, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        val row1 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        tools.addView(row1)

        addButton(row1, "Brush") {
            drawingView.isEraser = false
            toast("Brush enabled")
        }
        addButton(row1, "Eraser") {
            drawingView.isEraser = true
            toast("Eraser enabled")
        }
        addButton(row1, "Undo") { drawingView.undo() }
        addButton(row1, "Redo") { drawingView.redo() }
        addButton(row1, "Clear") {
            AlertDialog.Builder(this)
                .setTitle("Clear canvas?")
                .setMessage("This will clear your current drawing. Autosave will update after clearing.")
                .setPositiveButton("Clear") { _, _ -> drawingView.clearCanvas() }
                .setNegativeButton("Cancel", null)
                .show()
        }

        val row2 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        tools.addView(row2)
        addButton(row2, "Save PNG") { exportArtwork(GalleryExporter.ImageFormat.PNG) }
        addButton(row2, "Save JPG") { exportArtwork(GalleryExporter.ImageFormat.JPEG) }
        addButton(row2, "Login") { showLoginDialog() }
        addButton(row2, "Premium") { billing.openPremiumScreen(this) }

        val palette = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        tools.addView(palette)
        addColorButton(palette, Color.BLACK, "Black")
        addColorButton(palette, Color.RED, "Red")
        addColorButton(palette, Color.BLUE, "Blue")
        addColorButton(palette, Color.GREEN, "Green")
        addColorButton(palette, Color.rgb(255, 138, 0), "Orange")
        addColorButton(palette, Color.rgb(252, 217, 212), "Pink")
        addColorButton(palette, Color.rgb(125, 75, 25), "Brown")

        val sizeLabel = TextView(this).apply {
            text = "Brush size: 14"
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        tools.addView(sizeLabel)
        val sizeBar = SeekBar(this).apply {
            max = 80
            progress = 14
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val size = progress.coerceAtLeast(2)
                    drawingView.brushSize = size.toFloat()
                    sizeLabel.text = "Brush size: $size"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            })
        }
        tools.addView(sizeBar)

        adsContainer = LinearLayout(this).apply {
            gravity = Gravity.CENTER
            addView(AdsManager.createBanner(this@MainActivity))
        }
        root.addView(adsContainer, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        setContentView(root)
    }

    private fun addButton(parent: LinearLayout, text: String, action: () -> Unit) {
        val button = MaterialButton(this).apply {
            this.text = text
            textSize = 11f
            minWidth = 0
            minimumWidth = 0
            setPadding(8, 0, 8, 0)
            setOnClickListener { action() }
        }
        parent.addView(button, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
    }

    private fun addColorButton(parent: LinearLayout, color: Int, label: String) {
        val button = Button(this).apply {
            text = " "
            setBackgroundColor(color)
            contentDescription = label
            setOnClickListener {
                drawingView.currentColor = color
                toast("$label selected")
            }
        }
        parent.addView(button, LinearLayout.LayoutParams(0, 60, 1f))
    }

    private fun autosaveDraft() {
        val width = drawingView.width.coerceAtLeast(1)
        val height = drawingView.height.coerceAtLeast(1)
        database.saveLatestDraft("Auto saved artwork", drawingView.toJson(), width, height)
        analytics.track("draft_autosaved", JSONObject().put("width", width).put("height", height))
    }

    private fun loadLatestDraft() {
        database.loadLatestArtwork()?.let { latest ->
            drawingView.post {
                drawingView.loadJson(latest.strokesJson)
                toast("Latest progress loaded automatically")
            }
        }
    }

    private fun exportArtwork(format: GalleryExporter.ImageFormat) {
        autosaveDraft()
        val exportWidth = drawingView.width.coerceAtLeast(1080) * 3
        val exportHeight = drawingView.height.coerceAtLeast(1080) * 3
        val bitmap = drawingView.renderToBitmap(exportWidth, exportHeight)
        val uri = GalleryExporter.saveToGallery(this, bitmap, format)
        bitmap.recycle()
        if (uri != null) {
            analytics.track("artwork_exported", JSONObject().put("format", format.displayName).put("uri", uri.toString()))
            NotificationHelper.showSavedNotification(this)
            toast("Saved high-resolution ${format.displayName} to Gallery")
        } else {
            toast("Save failed. Please check storage permission.")
        }
    }

    private fun showLoginDialog() {
        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 0)
        }
        val email = EditText(this).apply {
            hint = "Email"
        }
        val password = EditText(this).apply {
            hint = "Password"
            inputType = 0x00000081
        }
        form.addView(email)
        form.addView(password)

        AlertDialog.Builder(this)
            .setTitle("Login / Register")
            .setView(form)
            .setPositiveButton("Login") { _, _ -> authAction(email.text.toString(), password.text.toString(), register = false) }
            .setNegativeButton("Register") { _, _ -> authAction(email.text.toString(), password.text.toString(), register = true) }
            .setNeutralButton("Cancel", null)
            .show()
    }

    private fun authAction(email: String, password: String, register: Boolean) {
        if (email.isBlank() || password.length < 6) {
            toast("Enter email and password with at least 6 characters")
            return
        }
        thread {
            try {
                val message = if (register) auth.register(email, password) else auth.login(email, password)
                runOnUiThread {
                    analytics.track(if (register) "register_success" else "login_success")
                    toast(message)
                }
            } catch (e: Exception) {
                runOnUiThread { toast(e.message ?: "Login failed. Is backend running?") }
            }
        }
    }

    private fun unlockPremiumLocally() {
        database.putState("premium", "true")
        adsContainer?.removeAllViews()
        toast("Premium unlocked")
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 2001)
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
