package app.bsc.db.drawing.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.bsc.db.drawing.R
import kotlinx.android.synthetic.main.enter_play_mode.view.*

class EnterPlayMode(): Fragment () {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater!!.inflate(R.layout.enter_play_mode, container, false)

        view.enterplaylayout.setOnClickListener { view ->
            val intent = Intent(context, DrawActivity::class.java)
            intent.putExtra("playMode", true)
            context!!.startActivity(intent)
        }

        // Return the fragment view/layout
        return view
    }

    init{

    }

}