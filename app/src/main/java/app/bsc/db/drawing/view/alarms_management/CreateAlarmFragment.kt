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
import android.util.Log
import android.widget.Toast
import app.bsc.db.drawing.R
import kotlinx.android.synthetic.main.fragment_one.*
import java.util.*

class CreateAlarmFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  inflater.inflate(R.layout.fragment_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_add.setOnClickListener {
            createAlarm()
        }
    }

    private fun createAlarm(){
        var alarmManager = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val calendar: Calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, timePicker.hour)
            set(Calendar.MINUTE, timePicker.minute)
            set(Calendar.MILLISECOND, 0)
        }

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(activity, "Created alarm, supposedly", Toast.LENGTH_SHORT).show()
    }

    class AlarmReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG, "onReceive: now i am running")
            Log.d(TAG, "onReceive: now i am running")
            Toast.makeText(context, "I'm running", Toast.LENGTH_LONG).show()

//            val fpvIntent = Intent(context, FingerPaintView::class.java)
//            fpvIntent.putExtra("lock", true)
//            fpvIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            context.startActivity(fpvIntent)
        }

        companion object {
            private val TAG = AlarmReceiver::class.java.name
        }
    }
}