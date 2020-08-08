package app.bsc.db.drawing.view.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import app.bsc.db.drawing.R
import app.bsc.db.drawing.data.local.DBHelper
import app.bsc.db.drawing.model.Alarm
import app.bsc.db.drawing.view.paint.DrawActivity
import kotlinx.android.synthetic.main.fragment_one.*
import java.util.*

class CreateAlarmFragment : Fragment() {
    companion object {
        var requestCode: Int = 0
        var viewRefreshListener: ViewRefreshListener? = null
    }

    lateinit var db: DBHelper
    private var dailyAlarm = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_add.setOnClickListener {

            val builder = AlertDialog.Builder(context!!)

            builder.setTitle(getString(R.string.create_alarm))

            builder.setMessage(getString(R.string.repeating_alarm_question))

            builder.setNegativeButton(getString(R.string.no)) { _, _ ->
                this.dailyAlarm = 0
                createAlarm()
            }

            builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                this.dailyAlarm = 1
                createAlarm()
            }

            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()

            // Display the alert dialog on app interface
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.show()

        }

        db = DBHelper(context!!)
        requestCode = db.getMaxReqId()
    }

    private fun createAlarm() {
        val alarmManager = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val currentDate: Calendar = Calendar.getInstance()
        val calendar: Calendar = Calendar.getInstance()
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, timePicker.hour)
            set(Calendar.MINUTE, timePicker.minute)
            set(Calendar.SECOND, 0)
        }

        calendar.set(Calendar.MILLISECOND, 0)

        if (calendar < currentDate)
            calendar.add(Calendar.DATE, 1)

        requestCode += 1

        val alarmObj = Alarm(
            timePicker.hour, timePicker.minute,
            requestCode, dailyAlarm
        )

        val intent = Intent(context, AlarmReceiver::class.java)

        intent.putExtra("requestId", alarmObj.reqId)
        intent.putExtra("daily", alarmObj.daily)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmObj.reqId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (this.dailyAlarm == 1)
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        addAlarmToDb(alarmObj)
        addAlarmToRecyclerView(alarmObj)

        if (viewRefreshListener != null)
            viewRefreshListener!!.updateView()
        else
            Toast.makeText(context, getString(R.string.refresh_alarms_failure), Toast.LENGTH_SHORT)
                .show()

        if (dailyAlarm == 1) {
            Toast.makeText(
                activity,
                getString(R.string.daily_alarm_confirmation),
                Toast.LENGTH_SHORT
            ).show()
        } else
            Toast.makeText(
                activity,
                getString(R.string.one_time_alarm_confirmation),
                Toast.LENGTH_SHORT
            ).show()
    }

    private fun addAlarmToDb(alarmObj: Alarm) {
        db.addAlarm(alarmObj)
    }

    private fun addAlarmToRecyclerView(alarmObj: Alarm) {
        ViewAlarmsFragment.addAlarm(alarmObj)
    }

    class AlarmReceiver : BroadcastReceiver() {
        companion object {
            private val TAG = AlarmReceiver::class.java.name
            var ringtone: Ringtone? = null
            fun stopAlarmRinging() {
                ringtone!!.stop()
            }
        }

        override fun onReceive(context: Context, intent: Intent) {
            var alarmUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }

            ringtone = RingtoneManager.getRingtone(context, alarmUri)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ringtone!!.isLooping = true
            }
            ringtone!!.play()

            val id = intent.getIntExtra("requestId", -2)
            val dailyStatus = intent.getIntExtra("daily", 0)

            val fpvIntent = Intent(context, DrawActivity::class.java).apply {
                putExtra("requestId", id)
                putExtra("lock", true)
                putExtra("daily", dailyStatus)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(fpvIntent)

            viewRefreshListener!!.updateView()
        }
    }
}