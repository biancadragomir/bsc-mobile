package app.bsc.db.drawing.view.alarms_management

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.bsc.db.drawing.R
import app.bsc.db.drawing.model.Alarm
import app.bsc.db.drawing.util.db.DBHelper
import app.bsc.db.drawing.view.DrawActivity
import java.util.ArrayList
import kotlin.math.log

interface MyListener {
    fun updateView()
    fun doDeleteActions(alarm: Alarm)
}

class ViewAlarmsFragment : Fragment(), AlarmsRecyclerAdapter.OnItemClickListener, MyListener {

    override fun updateView() {
        refreshData()
    }

    companion object{
        private val LOG_TAG = ViewAlarmsFragment::class.java.simpleName

        var recyclerView : RecyclerView? = null

        var alarmsList = ArrayList<Alarm>()

        fun addAlarm(alarm: Alarm){
            alarmsList.add(alarm)
//            refreshData()
        }

        fun deleteAlarm(reqId: Int){
            db!!.deleteAlarmById(reqId)

            for(alarm: Alarm in alarmsList){
                if(alarm.reqId == reqId){
                    alarmsList.remove(alarm)
                    Log.i(LOG_TAG, "alarm removed " + alarm.reqId)
                }
                break
            }

            // TODO also delete alarm from the recyclerview list
        }

        var db: DBHelper? = null

    }

    fun refreshData() {
        Log.i("ViewAlarmsFragment", "refreshData() was called")

        alarmsList = db!!.allAlarms
//        (recyclerView!!.adapter as AlarmsRecyclerAdapter).setDataset(alarmsList)

        val adapter = AlarmsRecyclerAdapter(alarmsList, this)
            recyclerView!!.adapter = adapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        CreateAlarmFragment.myListener = this
        DrawActivity.myListener = this

        return inflater.inflate(R.layout.fragment_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewAlarms)
        recyclerView!!.apply {
            layoutManager = LinearLayoutManager(this@ViewAlarmsFragment.context)
            adapter = AlarmsRecyclerAdapter(alarmsList, this@ViewAlarmsFragment)
        }

        db = DBHelper(context!!)
        refreshData()
    }

    fun cancelIntent(reqId: Int){
        val intent = Intent(context, CreateAlarmFragment.AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, reqId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        pendingIntent.cancel()
    }

    override fun doDeleteActions(alarm: Alarm){
        db!!.deleteAlarm(alarm)
        alarmsList.remove(alarm)
    }

    override fun onClick(position: Int) {
        Log.i("ViewAlarmsFragment", "pressed on click!" + alarmsList[position].hour+ ":"+alarmsList[position].minute)

        val builder = AlertDialog.Builder(context!!)

        builder.setTitle("Delete Alarm")

        builder.setMessage("Do you want to delete the alarm?")

        builder.setPositiveButton("YES"){_, _->

            cancelIntent(alarmsList[position].reqId)

            doDeleteActions(alarmsList[position])

            Toast.makeText(context,"Successfully deleted alarm.",Toast.LENGTH_SHORT).show()

            refreshData()
        }

        builder.setNeutralButton("CANCEL"){_,_->
            // Do something when user press the positive button
            Toast.makeText(context,"Cancelled deletion",Toast.LENGTH_SHORT).show()
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }
}