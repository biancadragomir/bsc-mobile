package app.bsc.db.drawing.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import app.bsc.db.drawing.R
import app.bsc.db.drawing.util.paint.FingerPaintView
import app.bsc.db.drawing.view.alarms_management.CreateAlarmFragment
import app.bsc.db.drawing.view.alarms_management.ViewAlarmsFragment
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private var adapter: TabAdapter? = null
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager = findViewById(R.id.viewPager)
        viewPager.setBackgroundResource(R.drawable.get);
        tabLayout = findViewById(R.id.tabLayout)
        adapter = TabAdapter(supportFragmentManager)
        adapter?.addFragment(CreateAlarmFragment(), "NEW ALARM")
        adapter?.addFragment(ViewAlarmsFragment(), "VIEW ALARMS")
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    fun getFpvIntent(): Intent{
        val fpvIntent = Intent(this, FingerPaintView::class.java)
        fpvIntent.putExtra("lock", true)
        fpvIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return fpvIntent
    }
}


