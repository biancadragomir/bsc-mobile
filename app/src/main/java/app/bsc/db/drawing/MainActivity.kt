package app.bsc.db.drawing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
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
        tabLayout = findViewById(R.id.tabLayout)
        adapter = TabAdapter(supportFragmentManager)
        adapter?.addFragment(Tab1Fragment(), "NEW")
        adapter?.addFragment(Tab2Fragment(), "VIEW ALARMS")
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

    }
}


