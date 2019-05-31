package app.bsc.db.drawing.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import app.bsc.db.drawing.R
import app.bsc.db.drawing.util.paint.FingerPaintView
import app.bsc.db.drawing.util.prediction.Classifier
import app.bsc.db.drawing.util.prediction.Result
import app.bsc.db.drawing.view.alarms_management.CreateAlarmFragment
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.drawing_layout.*

import java.io.IOException
import java.util.*
//
//class DrawActivity : AppCompatActivity() {
//
//    private var mclassifier: Classifier? = null
//
//    var mFpvPaint: FingerPaintView? = null
//
//    var mTxtPrediction: TextView? = null
//
//    private val animalsCategories = ArrayList(Arrays.asList("Bat", "Bear", "Bee", "Bird",
//            "Butterfly", "Camel", "Cat", "Dog",
//            "Dolphin", "Dragon", "Duck", "Elephant",
//            "Flamingo", "Fish", "Hedgehog", "Horse",
//            "Kangaroo", "Monkey", "Mosquito", "Octopus",
//            "Parrot", "Penguin", "Pig", "Rabbit",
//            "Raccoon", "Rhinoceros", "Scorpion", "Sheep",
//            "Snail", "Snake", "Spider", "Zebra"))
//
//    fun getRandomAnimal(): String{
//        val randomInteger = (0..31).shuffled().first()
//        return this.animalsCategories[randomInteger]
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.drawing_layout)
////
////        btnPredict.setOnClickListener {
////            this.onClick_btnPrediction()
////        }
//
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.drawing_layout)
//
//        this.mFpvPaint = findViewById<FingerPaintView>(R.id.fpv_paint)
//        this.mTxtPrediction = findViewById<TextView>(R.id.txtPrediction)
//
//        val mBtnPredict = findViewById<Button>(R.id.btnPredict)
//        mBtnPredict.setOnClickListener {
//            this.onClick_btnPrediction()
//        }
//
//        ButterKnife.bind(this)
//
//        val metrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(metrics)
//        init()
//
//        val animal = getRandomAnimal()
//        txtPrediction.text = String.format(getString(R.string.animal), animal)
//
//    }
//
//    private fun init() {
//        try {
//            mclassifier = Classifier(this@DrawActivity)
//        } catch (e: IOException) {
//            Toast.makeText(this, "Could not instantiate classifier.", Toast.LENGTH_SHORT).show()
//            Log.e("DRAW_ACTIVITY", "init(): Failed to create Classifier", e)
//        }
//
//    }
//
//    private fun renderResult(result: Result) {
//        mTxtPrediction!!.text = animalsCategories[result.number]
//        println(animalsCategories[result.number])
//    }
//
//    private fun onClick_btnPrediction() {
//        if(fpv_paint!!.isEmpty){
////            Toast.makeText(this, "Draw something first!", Toast.LENGTH_SHORT).show()
//        }else{
//            val image = fpv_paint!!.exportToBitmap(
//                Classifier.IMG_WIDTH, Classifier.IMG_HEIGHT)
//
//            val result = this.mclassifier!!.classify(image)
//            renderResult(result)
////            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
//            finish()
//        }
//    }
//}

class DrawActivity : Activity() {
    private var mclassifier: Classifier? = null

    var mTxtPrediction: TextView? = null
    var mFpvPaint: FingerPaintView? = null
    var mBtnSetAlarm: Button? = null

    private val animalsCategories = ArrayList(Arrays.asList("Bat", "Bear", "Bee", "Bird",
        "Butterfly", "Camel", "Cat", "Dog",
        "Dolphin", "Dragon", "Duck", "Elephant",
        "Flamingo", "Fish", "Hedgehog", "Horse",
        "Kangaroo", "Monkey", "Mosquito", "Octopus",
        "Parrot", "Penguin", "Pig", "Rabbit",
        "Raccoon", "Rhinoceros", "Scorpion", "Sheep",
        "Snail", "Snake", "Spider", "Zebra"))

    fun getRandomAnimal(): String{
        val randomInteger = (0..31).shuffled().first()
        return this.animalsCategories[randomInteger]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawing_layout)

        this.mFpvPaint = findViewById(R.id.fpv_paint) as app.bsc.db.drawing.util.paint.FingerPaintView
        this.mTxtPrediction = findViewById<TextView>(R.id.txtPrediction)

        val mBtnPredict = findViewById<Button>(R.id.btnPredict)
        mBtnPredict.setOnClickListener {
            this.onClick_btnPrediction()
        }
//        val mBtnSetAlarm = findViewById<Button>(R.id.btnSetAlarm)
//        mBtnSetAlarm.setOnClickListener{
//            this.startAlarmActivity()
//        }

        ButterKnife.bind(this)

        verifyStoragePermissions(this)

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        init()

        var animal = getRandomAnimal()
        txtPrediction.text = String.format(getString(R.string.animal), animal)

    }

    private fun startAlarmActivity() {
        val alarmActivityIntent = Intent(this, CreateAlarmFragment::class.java)
        startActivity(alarmActivityIntent)
    }

    private fun init() {
        try {
            mclassifier = Classifier(this)
        } catch (e: IOException) {
            Toast.makeText(this, "Could not instantiate classifier.", Toast.LENGTH_LONG).show()
            Log.e(LOG_TAG, "init(): Failed to create Classifier", e)
            e.stackTrace
        }

    }

    private fun renderResult(result: Result) {
        //        mTvPrediction.setText(String.valueOf(result.getNumber()));
        //        mTvPrediction.setText(animalsCategories.get(result.getNumber()));
        //        mTvProbability.setText(String.valueOf(result.getProbability()));
        //        mTvTimeCost.setText(String.format(getString(R.string.timecost_value), result.getTimeCost()));
        mTxtPrediction!!.text = animalsCategories[result.number]
        println(animalsCategories[result.number])
    }

    private fun onClick_btnPrediction() {
        if(mFpvPaint!!.isEmpty){
            Toast.makeText(this, "Draw something first!", Toast.LENGTH_SHORT).show()
        }else{
            val image = mFpvPaint!!.exportToBitmap(
                Classifier.IMG_WIDTH, Classifier.IMG_HEIGHT)

            if(mclassifier!=null){
                val result = this.mclassifier!!.classify(image)
                renderResult(result)
                CreateAlarmFragment.AlarmReceiver.stopAlarmRinging()
                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    companion object {
        private val LOG_TAG = DrawActivity::class.java.simpleName

        // Storage Permissions
        private val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        /**
         * Checks if the app has permission to write to device storage
         *
         * If the app does not has permission then the user will be prompted to grant permissions
         *
         * @param activity
         */
        fun verifyStoragePermissions(activity: Activity) {
            // Check if we have write permission
            val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
            }
        }
    }
}