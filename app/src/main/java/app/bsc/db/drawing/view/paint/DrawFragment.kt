package app.bsc.db.drawing.view.paint

import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import app.bsc.db.drawing.R
import app.bsc.db.drawing.data.network.NetworkClient
import app.bsc.db.drawing.data.network.NetworkConnectionChecker
import app.bsc.db.drawing.data.network.UploadAPIs
import app.bsc.db.drawing.data.prediction.Classifier
import app.bsc.db.drawing.view.main.MainActivity
import kotlinx.android.synthetic.main.drawing_fragment_layout.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class DrawFragment : Fragment() {
    private var mClassifier: Classifier? = null

    private val objectCategories = ArrayList(
        Arrays.asList(
            "Apple",
            "Banana",
            "Pineapple",
            "Pants",
            "Carrot",
            "Cup",
            "Anvil",
            "Bowtie",
            "Face",
            "Hand"
        )
    )

    private val nrObj = objectCategories.size

    private var connectedToNetwork: Boolean = true

    private fun getRandomObject(): String {
        val randomInteger = (0..(nrObj - 1)).shuffled().first()
        return this.objectCategories[randomInteger]
    }

    private var objectToDraw: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.drawing_fragment_layout, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btnPredictFragment.text = "Predict"
        btnPredictFragment.setOnClickListener {
            this.onClick_btnPrediction()
            MainActivity.viewPager.setPagingEnabled(true)
        }

        btnAbortFragment.setOnClickListener {
            Toast.makeText(context, "Alright!", Toast.LENGTH_SHORT).show()
            fpv_paint_fragment.clear()
            objectToDraw = getRandomObject()
            txtPredictionFragment.text =
                String.format(getString(R.string.strObjToDraw), objectToDraw)
            MainActivity.viewPager.setPagingEnabled(true)
        }
        btnAbortFragment.text = "Another one"

        drawing_layout_fragment.setOnClickListener {
            MainActivity.viewPager.setPagingEnabled(true)
        }

        objectToDraw = getRandomObject()
        txtPredictionFragment.text = String.format(getString(R.string.strObjToDraw), objectToDraw)
    }

    private fun init() {

        try {
            mClassifier = Classifier(activity!!)
        } catch (e: IOException) {
            Toast.makeText(context, "Could not instantiate classifier.", Toast.LENGTH_LONG).show()
            Log.e(LOG_TAG, "init(): Failed to create Classifier", e)
            e.stackTrace
        }
    }

    private fun onClick_btnPrediction() {
        if (fpv_paint_fragment.isEmpty) {
            Toast.makeText(context, "Draw something first!", Toast.LENGTH_SHORT).show()
        } else {

            val image = fpv_paint_fragment.exportToBitmap(
                Classifier.IMG_WIDTH, Classifier.IMG_HEIGHT
            )

            connectedToNetwork = true
            if (!NetworkConnectionChecker.isNetworkAvailable(context!!)) {
                connectedToNetwork = false
            } else {
                val filePath = saveImageToExternalStorage(image)
                uploadToServer(filePath)
            }

            if (mClassifier != null) {
                val result = this.mClassifier!!.classify(image)
                if (objectCategories[result.number] == objectToDraw) {
                    Toast.makeText(context, "Success! $objectToDraw", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        context,
                        "Recognized: ${objectCategories[result.number]}. Try again!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                if (!connectedToNetwork)
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()

                fpv_paint_fragment.clear()
                objectToDraw = getRandomObject()
                txtPredictionFragment.text =
                    String.format(getString(R.string.strObjToDraw), objectToDraw)
            }
        }
    }

    private fun saveImageToExternalStorage(finalBitmap: Bitmap): String {
        val root =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()

        val myDir = File("$root/snooze")
        val myDirCreated = myDir.mkdirs()
        println("My dir created: $myDirCreated")
        val fname = "image.jpg"

        val file = File(myDir, fname)
        if (file.exists())
            file.delete()

        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)

            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(this.context, arrayOf(file.toString()), null,
            object : MediaScannerConnection.OnScanCompletedListener {
                override fun onScanCompleted(path: String, uri: Uri) {
                    Log.i("ExternalStorage", "Scanned $path:")
                    Log.i("ExternalStorage", "-> uri=$uri")
                }
            })

        return file.absolutePath
    }

    private fun imageToString(bitmap: Bitmap): String {
        var byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        var imgByte = byteArrayOutputStream.toByteArray()

        return android.util.Base64.encodeToString(imgByte, android.util.Base64.DEFAULT)
    }


    private fun uploadToServer(filePath: String) {
        Log.i(LOG_TAG, "Trying to upload to server...")
        val retrofit = NetworkClient.getRetrofitClient(context!!)

        val uploadAPIs = retrofit!!.create(UploadAPIs::class.java)
        //Create a file object using file path
        val file = File(filePath)

        // Create a request body with file and image media type
        val fileReqBody = RequestBody.create(MediaType.parse("image/*"), file)

        // Create MultipartBody.Part using file request-body,file name and part name
        val part = MultipartBody.Part.createFormData("upload", file.getName(), fileReqBody)

        //Create request body with text description and text media type
        val description = RequestBody.create(MediaType.parse("text/plain"), "image-type")

        val call = uploadAPIs.uploadImage(part, description)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.body() != null) {
                    var predictionResultServer = response.body()!!.string().substringAfter(':')
                    predictionResultServer = predictionResultServer.substringAfter('"')
                    predictionResultServer = predictionResultServer.substringBefore('"')
                    Log.i("Retrofit success: ", predictionResultServer)
                    Toast.makeText(
                        context,
                        "Server result: " + predictionResultServer,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(context, "There's a problem!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, throwable: Throwable) {
                Toast.makeText(context, "Server request failed.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        private val LOG_TAG = DrawActivity::class.java.simpleName
    }
}