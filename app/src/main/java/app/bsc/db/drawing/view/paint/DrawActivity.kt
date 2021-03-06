package app.bsc.db.drawing.view.paint

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.bsc.db.drawing.R
import app.bsc.db.drawing.data.prediction.Classifier
import app.bsc.db.drawing.view.alarms.CreateAlarmFragment
import app.bsc.db.drawing.view.alarms.ViewAlarmsFragment
import app.bsc.db.drawing.view.alarms.ViewRefreshListener
import app.bsc.db.drawing.view.main.MainActivity
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.drawing_layout.*
import java.io.IOException
import java.util.*

class DrawActivity() : AppCompatActivity() {
    private var classifier: Classifier? = null

    private var textPredictionTextView: TextView? = null
    var fpvPaint: FingerPaintView? = null

    private val objectCategories = ArrayList(
        listOf(
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

    private fun getRandomObject(): String {
        val randomInteger = (0 until nrObj).shuffled().first()
        return this.objectCategories[randomInteger]
    }

    var objectToDraw: String? = null
    var reqId = 0
    var isDaily: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawing_layout)

        reqId = intent.getIntExtra("requestId", 0)

        isDaily = intent.getIntExtra("daily", 0)

        Log.i(LOG_TAG, "The alarm to be deleted has ID $reqId")

        if (isDaily == 0) {
            ViewAlarmsFragment.deleteAlarm(reqId)
            viewRefreshListener!!.updateView()
        }

        this.fpvPaint = findViewById(R.id.fpv_paint) as FingerPaintView
        this.textPredictionTextView = findViewById(R.id.txtPrediction)

        val mBtnPredict = findViewById<Button>(R.id.btnPredict)
        mBtnPredict.setOnClickListener {
            this.predictButtonOnClick()
            MainActivity.viewPager.setPagingEnabled(true)
        }

        val mBtnAbort = findViewById<Button>(R.id.btnAbort)
        mBtnAbort.setOnClickListener {
            finish()
            CreateAlarmFragment.AlarmReceiver.stopAlarmRinging()
            Toast.makeText(this, getString(R.string.cancelled), Toast.LENGTH_SHORT).show()
            MainActivity.viewPager.setPagingEnabled(true)
        }

        ButterKnife.bind(this)

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        init()

        objectToDraw = getRandomObject()
        txtPrediction.text = String.format(getString(R.string.strObjToDraw), objectToDraw)
    }

    private fun init() {

        try {
            classifier = Classifier(this)
        } catch (e: IOException) {
            Toast.makeText(this, "Could not instantiate classifier.", Toast.LENGTH_LONG).show()
            Log.e(LOG_TAG, "init(): Failed to create Classifier", e)
            e.stackTrace
        }
    }

    private fun predictButtonOnClick() {
        if (fpvPaint!!.isEmpty) {
            Toast.makeText(this, "Draw something first!", Toast.LENGTH_SHORT).show()
        } else {
            val image = fpvPaint!!.exportToBitmap(
                Classifier.IMG_WIDTH, Classifier.IMG_HEIGHT
            )

            if (classifier != null) {
                val result = this.classifier!!.classify(image)
                if (objectCategories[result.number] == objectToDraw) {
                    CreateAlarmFragment.AlarmReceiver.stopAlarmRinging()
                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Recognized: " + objectCategories[result.number] + ". Try again!",
                        Toast.LENGTH_SHORT
                    ).show()
                    fpv_paint.clear()
                    objectToDraw = getRandomObject()
                    txtPrediction.text =
                        String.format(getString(R.string.strObjToDraw), objectToDraw)
                }
            }
        }
    }

    companion object {
        private val LOG_TAG = DrawActivity::class.java.simpleName
        var viewRefreshListener: ViewRefreshListener? = null
    }
}