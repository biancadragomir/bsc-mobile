package app.bsc.db.drawing.view.main

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.app.Activity
import app.bsc.db.drawing.R
import app.bsc.db.drawing.view.alarms.CreateAlarmFragment
import app.bsc.db.drawing.view.alarms.ViewAlarmsFragment
import app.bsc.db.drawing.view.paint.DrawFragment

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var viewPager: CustomViewPager
    }

    private var adapter: TabAdapter? = null
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        verifyStoragePermissions(this)

        viewPager = findViewById(R.id.viewPager)
        viewPager.setBackgroundResource(R.drawable.get)
        tabLayout = findViewById(R.id.tabLayout)
        adapter = TabAdapter(supportFragmentManager)

        adapter?.addFragment(CreateAlarmFragment(), "NEW ALARM")
        adapter?.addFragment(ViewAlarmsFragment(), "VIEW ALARMS")
        adapter?.addFragment(DrawFragment(), "PLAY MODE")

        viewPager.adapter = adapter

        tabLayout.setupWithViewPager(viewPager)
    }

    // Storage Permissions
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE =
        arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    fun verifyStoragePermissions(activity: Activity) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }
}


