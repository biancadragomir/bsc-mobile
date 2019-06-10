package app.bsc.db.drawing.util.prediction

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
import java.util.Arrays

class Classifier @Throws(IOException::class)
constructor(activity: Activity) {

    private val options = Interpreter.Options()
    private val mInterpreter: Interpreter
    private val mImageData: ByteBuffer
    private val mImagePixels = IntArray(IMG_HEIGHT * IMG_WIDTH)
    private val mResult = Array(1) { FloatArray(NUM_CLASSES) }

    init {
        mInterpreter = Interpreter(loadModelFile(activity), options)
        mImageData = ByteBuffer.allocateDirect(
                4 * BATCH_SIZE * IMG_HEIGHT * IMG_WIDTH * NUM_CHANNEL)
        mImageData!!.order(ByteOrder.nativeOrder())
    }

    fun classify(bitmap: Bitmap): Result {
        convertBitmapToByteBuffer(bitmap)
        val startTime = SystemClock.uptimeMillis()
        mInterpreter.run(mImageData, mResult)
        val endTime = SystemClock.uptimeMillis()
        val timeCost = endTime - startTime
        Log.v(LOG_TAG, "classify(): result = " + Arrays.toString(mResult[0])
                + ", timeCost = " + timeCost)

        return Result(mResult[0], timeCost)
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
        if (mImageData == null) {
            return
        }
        mImageData.rewind()
        bitmap.getPixels(mImagePixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        var pixel = 0
        for (i in 0 until IMG_WIDTH) {
            for (j in 0 until IMG_HEIGHT) {
                val value = mImagePixels[pixel++]
                mImageData.putFloat(convertPixel(value))
            }
        }
    }

    companion object {
        private val LOG_TAG = Classifier::class.java.simpleName

        private val MODEL_NAME = "objects2.tflite"

        private val BATCH_SIZE = 1
        val IMG_HEIGHT = 28
        val IMG_WIDTH = 28
        private val NUM_CHANNEL = 1
        private val NUM_CLASSES = 15

        private fun convertPixel(color: Int): Float {
            return (255 - ((color shr 16 and 0xFF) * 0.299f
                    + (color shr 8 and 0xFF) * 0.587f
                    + (color and 0xFF) * 0.114f)) / 255.0f
        }
    }
}
