package app.bsc.db.drawing.data.prediction

import android.app.Activity
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class Classifier @Throws(IOException::class)
constructor(activity: Activity) {

    private val options = Interpreter.Options()
    private val interpreter: Interpreter
    private val imageData: ByteBuffer
    private val imagePixels = IntArray(IMG_HEIGHT * IMG_WIDTH)
    private val result = Array(1) { FloatArray(NUM_CLASSES) }

    init {
        interpreter = Interpreter(loadModelFile(activity), options)
        imageData = ByteBuffer.allocateDirect(
            4 * BATCH_SIZE * IMG_HEIGHT * IMG_WIDTH * NUM_CHANNEL
        )
        imageData.order(ByteOrder.nativeOrder())
    }

    fun classify(bitmap: Bitmap): Result {
        convertBitmapToByteBuffer(bitmap)
        val startTime = SystemClock.uptimeMillis()
        interpreter.run(imageData, result)
        val endTime = SystemClock.uptimeMillis()
        val timeCost = endTime - startTime
        Log.v(
            LOG_TAG, "classify(): result = " + result[0].contentToString()
                    + ", timeCost = " + timeCost
        )

        return Result(result[0], timeCost)
    }

    @Throws(IOException::class)
    private fun loadModelFile(activity: Activity): MappedByteBuffer {
        val fileDescriptor = activity.assets.openFd(MODEL_NAME)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap) {

        imageData.rewind()
        bitmap.getPixels(
            imagePixels,
            0,
            bitmap.width,
            0,
            0,
            bitmap.width,
            bitmap.height
        )

        var pixel = 0
        for (i in 0 until IMG_WIDTH) {
            for (j in 0 until IMG_HEIGHT) {
                val value = imagePixels[pixel++]
                imageData.putFloat(convertPixel(value))
            }
        }
    }

    companion object {
        private val LOG_TAG = Classifier::class.java.simpleName

        private const val MODEL_NAME = "convmodel-flask.tflite"
        private const val NUM_CLASSES = 10

        private const val BATCH_SIZE = 1
        const val IMG_HEIGHT = 28
        const val IMG_WIDTH = 28
        private const val NUM_CHANNEL = 1

        private fun convertPixel(color: Int): Float {
            return (255 - ((color shr 16 and 0xFF) * 0.299f
                    + (color shr 8 and 0xFF) * 0.587f
                    + (color and 0xFF) * 0.114f)) / 255.0f
        }
    }
}
