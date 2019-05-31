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

class CreateAlarmFragment : Fragment() {

    companion object{
        var requestCode = 0
    }

    lateinit var db: DBHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  inflater.inflate(R.layout.fragment_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_add.setOnClickListener {
            createAlarm()
        }
        db = DBHelper(context!!)
    }

    private fun createAlarm(){
        val alarmManager = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val calendar: Calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, timePicker.hour)
            set(Calendar.MINUTE, timePicker.minute)
            set(Calendar.MILLISECOND, 0)
        }

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        val alarmObj = Alarm(timePicker.hour, timePicker.minute, requestCode)
        addAlarmToDb(alarmObj)
        addAlarmToListView(alarmObj)
        requestCode++

        ViewAlarmsFragment.refreshData()
        println(db.allAlarms)
        Log.i("CreateAlarmFragment", "Refreshed after adding alarm!")

        Toast.makeText(activity, "Created alarm, supposedly", Toast.LENGTH_SHORT).show()
    }

    private fun addAlarmToDb(alarmObj: Alarm){
        // add to the database
        db.addAlarm(alarmObj)
    }

    private fun addAlarmToListView(alarmObj: Alarm){
        // add to the list view
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

            val fpvIntent = Intent(context, DrawActivity::class.java)
            fpvIntent.putExtra("lock", true)
            fpvIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(fpvIntent)
        }
    }
}