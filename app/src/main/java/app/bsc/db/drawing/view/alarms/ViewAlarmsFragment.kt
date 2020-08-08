package app.bsc.db.drawing.view.alarms

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.bsc.db.drawing.R
import app.bsc.db.drawing.data.local.DBHelper
import app.bsc.db.drawing.model.Alarm
import app.bsc.db.drawing.view.paint.DrawActivity
import java.util.*

interface ViewRefreshListener {
    fun updateView()
    fun doDeleteActions(alarm: Alarm)
}

class ViewAlarmsFragment : Fragment(),
    AlarmsRecyclerAdapter.OnItemClickListener,
    ViewRefreshListener {

    override fun updateView() {
        refreshData()
    }

    companion object {
        private val LOG_TAG = ViewAlarmsFragment::class.java.simpleName

        var recyclerView: RecyclerView? = null

        var alarmsList = ArrayList<Alarm>()

        fun addAlarm(alarm: Alarm) {
            alarmsList.add(alarm)
        }

        fun deleteAlarm(reqId: Int) {
            db!!.deleteAlarmById(reqId)

            for (alarm: Alarm in alarmsList) {
                if (alarm.reqId == reqId) {
                    alarmsList.remove(alarm)
                    Log.i(LOG_TAG, "Alarm removed from list: " + alarm.reqId)
                }
                break
            }
        }

        var db: DBHelper? = null

    }

    private fun refreshData() {
        Log.i("ViewAlarmsFragment", "refreshData() was called")

        alarmsList = db!!.allAlarms

        val adapter = AlarmsRecyclerAdapter(
            alarmsList,
            this
        )
        recyclerView!!.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        CreateAlarmFragment.viewRefreshListener = this
        DrawActivity.viewRefreshListener = this

        return inflater.inflate(R.layout.fragment_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewAlarms)
        recyclerView!!.apply {
            layoutManager = LinearLayoutManager(this@ViewAlarmsFragment.context)
            adapter = AlarmsRecyclerAdapter(
                alarmsList,
                this@ViewAlarmsFragment
            )
        }

        db = DBHelper(context!!)
        refreshData()
    }

    private fun cancelIntent(reqId: Int) {
        val intent = Intent(context, CreateAlarmFragment.AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, reqId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        pendingIntent.cancel()
    }

    override fun doDeleteActions(alarm: Alarm) {
        db!!.deleteAlarm(alarm)
        alarmsList.remove(alarm)
    }

    override fun onClick(position: Int) {
        val builder = AlertDialog.Builder(context!!)

        builder.setTitle(getString(R.string.delete_alarm))

        builder.setMessage(getString(R.string.delete_alarm_confirmation_message))

        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->

            cancelIntent(alarmsList[position].reqId)

            doDeleteActions(alarmsList[position])

            Toast.makeText(
                context,
                getString(R.string.delete_alarm_confirmation),
                Toast.LENGTH_SHORT
            ).show()

            refreshData()
        }

        builder.setNeutralButton(getString(R.string.cancel)) { _, _ ->
            // Do something when user press the positive button
            Toast.makeText(
                context,
                getString(R.string.cancelled_deletion_confirmation),
                Toast.LENGTH_SHORT
            ).show()
        }

        val dialog: AlertDialog = builder.create()

        dialog.show()
    }
}