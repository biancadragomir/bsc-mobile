package app.bsc.db.drawing.view.paint

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import app.bsc.db.drawing.view.main.MainActivity
import kotlin.math.abs

class FingerPaintView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val path = Path()
    private lateinit var drawingBitmap: Bitmap
    private lateinit var drawingCanvas: Canvas
    private val drawingPaint = Paint(Paint.DITHER_FLAG)
    private var penX: Float = 0.toFloat()
    private var penY: Float = 0.toFloat()

    private var pen: Paint = buildDefaultPen()
    var isEmpty: Boolean = true
        private set

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawingCanvas = Canvas(drawingBitmap)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(drawingBitmap, 0f, 0f, drawingPaint)
        canvas?.drawPath(path, pen)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        MainActivity.viewPager.setPagingEnabled(false)
        if (event == null) return false
        isEmpty = false
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onTouchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                onTouchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                onTouchUp()
                performClick()
                invalidate()
            }
        }
        super.onTouchEvent(event)
        return true

    }

    fun clear() {
        path.reset()
        drawingBitmap = Bitmap.createBitmap(
            drawingBitmap.width, drawingBitmap.height,
            Bitmap.Config.ARGB_8888
        )
        drawingCanvas = Canvas(drawingBitmap)
        isEmpty = true
        invalidate()
    }

    private fun exportToBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.BLACK)
        }
        draw(canvas)

        return bitmap
    }

    fun exportToBitmap(width: Int, height: Int): Bitmap {
        val rawBitmap = exportToBitmap()
        val scaledBitmap = Bitmap.createScaledBitmap(rawBitmap, width, height, false)
        rawBitmap.recycle()
        return scaledBitmap
    }

    private fun onTouchStart(x: Float, y: Float) {
        path.reset()
        path.moveTo(x, y)
        penX = x
        penY = y
    }

    private fun onTouchMove(x: Float, y: Float) {
        val dx = abs(x - penX)
        val dy = abs(y - penY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(penX, penY, (x + penX) / 2, (y + penY) / 2)
            penX = x
            penY = y
        }
    }

    private fun onTouchUp() {
        path.lineTo(penX, penY)
        drawingCanvas.drawPath(path, pen)
        path.reset()
    }

    companion object {
        private const val TOUCH_TOLERANCE = 4f
        private const val PEN_SIZE = 60f

        @JvmStatic
        private fun buildDefaultPen(): Paint = Paint().apply {
            this.isAntiAlias = true
            this.isDither = true
            this.color = Color.BLACK
            this.style = Paint.Style.STROKE
            this.strokeJoin = Paint.Join.ROUND
            this.strokeCap = Paint.Cap.ROUND
            this.strokeWidth = PEN_SIZE
        }
    }
}