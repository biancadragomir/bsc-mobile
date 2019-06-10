package app.bsc.db.drawing.view

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import app.bsc.db.drawing.R
import app.bsc.db.drawing.util.paint.FingerPaintView
import app.bsc.db.drawing.util.prediction.Classifier
import app.bsc.db.drawing.util.prediction.Result
import app.bsc.db.drawing.view.alarms_management.CreateAlarmFragment
import app.bsc.db.drawing.view.alarms_management.MyListener
import app.bsc.db.drawing.view.alarms_management.ViewAlarmsFragment
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.drawing_layout.*

import java.io.IOException
import java.util.*

class DrawActivity : Activity() {
    private var mclassifier: Classifier? = null

    var mTxtPrediction: TextView? = null
    var mFpvPaint: FingerPaintView? = null

//    private val animalsCategories = ArrayList(Arrays.asList("Bat", "Bear", "Bee", "Bird",
//        "Butterfly", "Camel", "Cat", "Dog",
//        "Dolphin", "Dragon", "Duck", "Elephant",
//        "Flamingo", "Fish", "Hedgehog", "Horse",
//        "Kangaroo", "Monkey", "Mosquito", "Octopus",
//        "Parrot", "Penguin", "Pig", "Rabbit",
//        "Raccoon", "Rhinoceros", "Scorpion", "Sheep",
//        "Snail", "Snake", "Spider", "Zebra"))


        private val animalsCategories = ArrayList(Arrays.asList(
             "Carrot",  "Cloud",  "Dog",
    "Mushroom",  "Pants",  "Penguin", "Pillow",
     "Snake", "Spider", "Stitches", "Table",
    "Tooth", "Triangle",  "Vase", "Zigzag"))


    private fun getRandomAnimal(): String{
        val randomInteger = (0..14).shuffled().first()
        return this.animalsCategories[randomInteger]
    }
    var animal: String? = null
    var reqId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawing_layout)

        reqId = intent.getIntExtra("requestId", 0)
        Log.i(LOG_TAG, "The req id to delete is " + reqId)
        ViewAlarmsFragment.deleteAlarm(reqId)
        myListener!!.updateView()

        this.mFpvPaint = findViewById(R.id.fpv_paint) as app.bsc.db.drawing.util.paint.FingerPaintView
        this.mTxtPrediction = findViewById(R.id.txtPrediction)

        val mBtnPredict = findViewById<Button>(R.id.btnPredict)
        mBtnPredict.setOnClickListener {
            this.onClick_btnPrediction()
        }

        val mBtnAbort= findViewById<Button>(R.id.btnAbort)
        mBtnAbort.setOnClickListener {
            finish()
            CreateAlarmFragment.AlarmReceiver.stopAlarmRinging()
            Toast.makeText(this, "Cancelled!", Toast.LENGTH_SHORT).show()
        }

        ButterKnife.bind(this)

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        init()

        animal = getRandomAnimal()
        txtPrediction.text = String.format(getString(R.string.animal), animal)
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
//                renderResult(result)
                if(animalsCategories[result.number] == animal){
                    CreateAlarmFragment.AlarmReceiver.stopAlarmRinging()
                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(this, "That's a " + animalsCategories[result.number] + ", try again!", Toast.LENGTH_SHORT).show()
                    fpv_paint.clear()
                    animal = getRandomAnimal()
                    txtPrediction.text = String.format(getString(R.string.animal), animal)
                }
            }
        }
    }

    companion object {
        private val LOG_TAG = DrawActivity::class.java.simpleName
        var myListener: MyListener? = null
    }
}