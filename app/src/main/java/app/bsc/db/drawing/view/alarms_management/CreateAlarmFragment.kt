package app.bsc.db.drawing.view.alarms_management
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
import kotlinx.android.synthetic.main.fragment_one.*
import java.util.*
import app.bsc.db.drawing.R
import app.bsc.db.drawing.model.Alarm
import app.bsc.db.drawing.util.db.DBHelper
import app.bsc.db.drawing.view.DrawActivity
import java.sql.Date
import java.time.DayOfWeek

class CreateAlarmFragment : Fragment() {
    companion object{
        var requestCode: Int = 0
        var myListener: MyListener? = null
    }

    lateinit var db: DBHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(this.tag, "value of request code in ON CREATE VIEW is "+ requestCode.toString())
        return  inflater.inflate(R.layout.fragment_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(this.tag, "value of request code  ON VIEW CREATED BEFORE DB CALL FOR MAX is "+ requestCode.toString())
        btn_add.setOnClickListener {
            createAlarm()
        }

        db = DBHelper(context!!)
        requestCode = db.getMaxReqId()
        Log.i(this.tag, "value of request code  ON VIEW CREATED --AFTER-- DB CALL FOR MAX is "+ requestCode.toString())
    }

    private fun createAlarm(){
        val alarmManager = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val currentDate: Calendar = Calendar.getInstance()
        val calendar: Calendar = Calendar.getInstance()
        Log.i(tag, "calendar --BEFORE-- time is " + calendar.time.toString())
        Log.i(tag, "AND TIMEZONE: " + calendar.timeZone)

        calendar.
            apply {
            set(Calendar.HOUR_OF_DAY, timePicker.hour)
            set(Calendar.MINUTE, timePicker.minute)
            set(Calendar.SECOND, 0)
        }

        calendar.set(Calendar.MILLISECOND, 0)

        //TODO uncomment the following two lines for preventing the alarm from ringing instantly due to being set in the past
//        if(calendar.compareTo(currentDate) < 0)
//             calendar.add(Calendar.DATE, 1)

        Log.i(tag, "calendar time is " + calendar.time.toString())

        val alarmObj = Alarm(timePicker.hour, timePicker.minute, ++requestCode)
        Log.i("-", "alarm request code is "+alarmObj.reqId)

        val intent = Intent(context, AlarmReceiver::class.java)
        Log.i("-", "new intent created")

        intent.putExtra("requestId", alarmObj.reqId)

        val pendingIntent = PendingIntent.getBroadcast(context, alarmObj.reqId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        addAlarmToDb(alarmObj)
        addAlarmToRecyclerView(alarmObj)

        if(myListener!=null)
            myListener!!.updateView()
        else
            Toast.makeText(context, "Could not refresh alarms!", Toast.LENGTH_SHORT).show()

        Toast.makeText(activity, "Created alarm, supposedly", Toast.LENGTH_SHORT).show()
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

            val fpvIntent = Intent(context, DrawActivity::class.java)
            fpvIntent.putExtra("requestId", id)
            fpvIntent.putExtra("lock", true)

            fpvIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(fpvIntent)

            myListener!!.updateView()

        }
    }
}