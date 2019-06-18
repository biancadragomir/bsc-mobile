package app.bsc.db.drawing.view.alarms
import android.app.AlarmManager
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.fragment_one.*
import java.util.*
import app.bsc.db.drawing.R
import app.bsc.db.drawing.model.Alarm
import app.bsc.db.drawing.data.local.DBHelper
import app.bsc.db.drawing.view.paint.DrawActivity

class CreateAlarmFragment : Fragment() {
    companion object{
        var requestCode: Int = 0
        var viewRefreshListener: ViewRefreshListener? = null
    }

    lateinit var db: DBHelper
    var dailyAlarm = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(this.tag, "value of request code in ON CREATE VIEW is "+ requestCode.toString())
        return  inflater.inflate(R.layout.fragment_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(this.tag, "value of request code  ON VIEW CREATED BEFORE DB CALL FOR MAX is "+ requestCode.toString())
        btn_add.setOnClickListener {

            val builder = AlertDialog.Builder(context!!)

            builder.setTitle("Create Alarm")

            builder.setMessage("Do you want the alarm to repeat?")

            builder.setNegativeButton("NO"){_,_->
                this.dailyAlarm = 0
                createAlarm()
            }

            builder.setPositiveButton("YES"){_, _->
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


//        switchDaily.setOnCheckedChangeListener { _, isChecked ->
//            dailyAlarm = isChecked
//        }

        db = DBHelper(context!!)
        requestCode = db.getMaxReqId()

        Log.i(this.tag, "value of request code  ON VIEW CREATED --AFTER-- DB CALL FOR MAX is "+ requestCode.toString())
    }

    private fun createAlarm(){
        val alarmManager = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val currentDate: Calendar = Calendar.getInstance()
        val calendar: Calendar = Calendar.getInstance()
        calendar.
            apply {
            set(Calendar.HOUR_OF_DAY, timePicker.hour)
            set(Calendar.MINUTE, timePicker.minute)
            set(Calendar.SECOND, 0)
        }

        calendar.set(Calendar.MILLISECOND, 0)

        if(calendar.compareTo(currentDate) < 0)
             calendar.add(Calendar.DATE, 1)

        requestCode += 1

        Log.i(tag, "creating alarm with requestCode = " + requestCode)
        val alarmObj = Alarm(timePicker.hour, timePicker.minute,
            requestCode, dailyAlarm)

        val intent = Intent(context, AlarmReceiver::class.java)

        intent.putExtra("requestId", alarmObj.reqId)
        intent.putExtra("daily", alarmObj.daily)

        val pendingIntent = PendingIntent.getBroadcast(context, alarmObj.reqId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if(this.dailyAlarm == 1)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        addAlarmToDb(alarmObj)
        addAlarmToRecyclerView(alarmObj)

        if(viewRefreshListener !=null)
            viewRefreshListener!!.updateView()
        else
            Toast.makeText(context, "Could not refresh alarms!", Toast.LENGTH_SHORT).show()

        if(dailyAlarm == 1)
            Toast.makeText(activity, "Created daily alarm!", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(activity, "Created one time alarm!", Toast.LENGTH_SHORT).show()
    }

    private fun addAlarmToDb(alarmObj: Alarm){
        db.addAlarm(alarmObj)
    }

    private fun addAlarmToRecyclerView(alarmObj: Alarm){
        ViewAlarmsFragment.addAlarm(alarmObj)
    }

    class AlarmReceiver : BroadcastReceiver() {
        companion object {
            private val TAG = AlarmReceiver::class.java.name
            var ringtone : Ringtone? = null
            fun stopAlarmRinging(){
                ringtone!!.stop()
            }
        }

        override fun onReceive(context: Context, intent: Intent) {
            var alarmUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }

            ringtone = RingtoneManager.getRingtone(context, alarmUri)
            ringtone!!.isLooping = true
            ringtone!!.play()

            var id = intent.getIntExtra("requestId", -2)
            val dailyStatus= intent.getIntExtra("daily", 0)

            val fpvIntent = Intent(context, DrawActivity::class.java)
            fpvIntent.putExtra("requestId", id)
            fpvIntent.putExtra("lock", true)
            fpvIntent.putExtra("daily", dailyStatus)

            fpvIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(fpvIntent)

            viewRefreshListener!!.updateView()

        }
    }
}