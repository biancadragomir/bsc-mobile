package app.bsc.db.drawing.view

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.bsc.db.drawing.R
import app.bsc.db.drawing.util.paint.FingerPaintView
import app.bsc.db.drawing.util.prediction.Classifier
import app.bsc.db.drawing.util.prediction.Result
import kotlinx.android.synthetic.main.drawing_layout.*

import java.io.IOException
import java.util.*

class DrawActivity : AppCompatActivity() {

    private var mclassifier: Classifier? = null

    var mTxtPrediction: TextView? = null
    var mFpvPaint: FingerPaintView? = null

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
        setContentView(R.layout.activity_main)

        this.mFpvPaint = findViewById(R.id.fpv_paint) as FingerPaintView
        this.mTxtPrediction = findViewById(R.id.txtPrediction)

        val mBtnPredict = findViewById<Button>(R.id.btnPredict)
        mBtnPredict.setOnClickListener {
            this.onClick_btnPrediction()
        }

//        ButterKnife.bind(this)

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        init()

        var animal = getRandomAnimal()
        txtPrediction.text = String.format(getString(R.string.animal), animal)

    }

    private fun init() {
        try {
            mclassifier = Classifier(this)
        } catch (e: IOException) {
            Toast.makeText(this, "Could not instantiate classifier.", Toast.LENGTH_LONG).show()
            Log.e("DRAW_ACTIVITY", "init(): Failed to create Classifier", e)
        }

    }

    private fun renderResult(result: Result) {
        mTxtPrediction!!.text = animalsCategories[result.number]
        println(animalsCategories[result.number])
    }

    /**
     * the CLOSE button
     */
    private fun onClick_btnPrediction() {
        if(mFpvPaint!!.isEmpty){
            Toast.makeText(this, "Draw something first!", Toast.LENGTH_SHORT).show()
        }else{
            val image = mFpvPaint!!.exportToBitmap(
                Classifier.IMG_WIDTH, Classifier.IMG_HEIGHT)

            val result = this.mclassifier!!.classify(image)
            renderResult(result)
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}