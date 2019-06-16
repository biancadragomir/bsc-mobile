package app.bsc.db.drawing.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import app.bsc.db.drawing.R
import app.bsc.db.drawing.util.prediction.Classifier
import kotlinx.android.synthetic.main.drawing_layout.*

import java.io.IOException
import java.util.*

class DrawFragment: Fragment() {
    private var mClassifier: Classifier? = null

    private val objectCategories = ArrayList(Arrays.asList(
        "Apple", "Banana",  "Pineapple", "Pants", "Carrot", "Cup",  "Anvil",  "Bowtie", "Face", "Hand" ))

    private val nrObj = objectCategories.size

    private fun getRandomObject(): String{
        val randomInteger = (0..(nrObj-1)).shuffled().first()
        return this.objectCategories[randomInteger]
    }
    var objectToDraw: String? = null
    var reqId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  inflater.inflate(R.layout.drawing_layout, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val metrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(metrics)
        init()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btnPredict.setOnClickListener {
            this.onClick_btnPrediction()
            MainActivity.viewPager.setPagingEnabled(true)
        }

        btnAbort.setOnClickListener {
            Toast.makeText(context, "Oh no...", Toast.LENGTH_SHORT).show()
            fpv_paint.clear()
            objectToDraw = getRandomObject()
            txtPrediction.text = String.format(getString(R.string.animal), objectToDraw)
            MainActivity.viewPager.setPagingEnabled(true)
        }

        drawing_layout.setOnClickListener{
            MainActivity.viewPager.setPagingEnabled(true)
        }

        objectToDraw = getRandomObject()
        txtPrediction.text = String.format(getString(R.string.animal), objectToDraw)
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
        if(fpv_paint!!.isEmpty){
            Toast.makeText(context, "Draw something first!", Toast.LENGTH_SHORT).show()
        }else{
            val image = fpv_paint!!.exportToBitmap(
                Classifier.IMG_WIDTH, Classifier.IMG_HEIGHT)

            if(mClassifier!=null){
                val result = this.mClassifier!!.classify(image)
                if(objectCategories[result.number] == objectToDraw){
                    Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "That's a " + objectCategories[result.number] + ", try again!", Toast.LENGTH_SHORT).show()
                }

                fpv_paint.clear()
                objectToDraw = getRandomObject()
                txtPrediction.text = String.format(getString(R.string.animal), objectToDraw)
            }
        }
    }

    companion object {
        private val LOG_TAG = DrawActivity::class.java.simpleName
    }
}