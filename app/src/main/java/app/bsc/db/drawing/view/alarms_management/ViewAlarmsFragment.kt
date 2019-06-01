package app.bsc.db.drawing.view.alarms_management

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.bsc.db.drawing.R
import app.bsc.db.drawing.model.Alarm
import app.bsc.db.drawing.util.db.DBHelper
import kotlinx.android.synthetic.main.list_item.*
import java.util.ArrayList

class ViewAlarmsFragment : Fragment(), AlarmsRecyclerAdapter.onItemClickListener {
    companion object{
        var recyclerView : RecyclerView? = null

        var alarmsList = ArrayList<Alarm>()

        fun addAlarm(alarm: Alarm){
            alarmsList.add(alarm)
        }

        var db: DBHelper? = null

        fun refreshData() {
            Log.i("ViewAlarmsFragment", "refreshData() was called")
            alarmsList = db!!.allAlarms
            val adapter = AlarmsRecyclerAdapter(alarmsList)
            recyclerView!!.adapter = adapter
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewAlarms)
        recyclerView!!.apply {
            layoutManager = LinearLayoutManager(this@ViewAlarmsFragment.context)
            adapter = AlarmsRecyclerAdapter(alarmsList)
        }

        db = DBHelper(context!!)
        refreshData()
    }


}