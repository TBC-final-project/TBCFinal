package com.c0d3in3.finalproject.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.ui.dashboard.home.HomeFragment
import com.c0d3in3.finalproject.ui.dashboard.notifications.NotificationsFragment
import com.c0d3in3.finalproject.ui.dashboard.search.SearchFragment
import com.c0d3in3.finalproject.ui.dashboard.stories.StoriesFragment
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_layout.view.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var adapter: DashboardPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        adapter = DashboardPagerAdapter(supportFragmentManager)

        adapter.addFragment(HomeFragment())
        adapter.addFragment(StoriesFragment())
        adapter.addFragment(NotificationsFragment())
        adapter.addFragment(SearchFragment())

        init()
        dashboardPager.adapter = adapter


    }

    private fun init(){
        setToolbarTitle(getString(R.string.news_feed))

        setSupportActionBar(toolbarLayout.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.mipmap.ic_launcher)
        supportActionBar!!.setDisplayShowTitleEnabled(false)


        addViewPagerListener()
        addNavMenuListener()
    }

    private fun addNavMenuListener(){
        nav_view.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navHome -> {
                    dashboardPager.setCurrentItem(0, true)
                    setToolbarTitle(getString(R.string.news_feed))
                }
                R.id.navStories -> {
                    dashboardPager.setCurrentItem(1, true)
                    setToolbarTitle(getString(R.string.stories))
                }
                R.id.navNotifications -> {
                    dashboardPager.setCurrentItem(2, true)
                    setToolbarTitle(getString(R.string.notifications))
                }
                R.id.navSearch -> {
                    dashboardPager.setCurrentItem(3, true)
                    setToolbarTitle(getString(R.string.search))
                }
            }

            true
        }
    }

    private fun addViewPagerListener(){
        dashboardPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                nav_view.menu.getItem(position).isChecked = true
            }

        })
    }

    private fun setToolbarTitle(title: String){
        toolbarLayout.titleTV.text = title
    }
}
