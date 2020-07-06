package com.c0d3in3.finalproject.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.c0d3in3.finalproject.BasePagerAdapter
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.network.PostsRepository
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.network.model.PostModel
import com.c0d3in3.finalproject.ui.auth.UserInfo
import com.c0d3in3.finalproject.ui.dashboard.home.HomeFragment
import com.c0d3in3.finalproject.ui.dashboard.notifications.NotificationsFragment
import com.c0d3in3.finalproject.ui.dashboard.search.SearchFragment
import com.c0d3in3.finalproject.ui.dashboard.stories.StoriesFragment
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private lateinit var adapter: BasePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        adapter =
            BasePagerAdapter(supportFragmentManager)

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

        addPostButton.setOnClickListener {
            addPosts()
        }

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

    private fun addPosts(){
        val post = PostModel()
        post.postId = "${(1..1000).random()}"
        post.postAuthor = UserInfo.userInfo
        post.postTimestamp = System.currentTimeMillis()
        post.postComments = arrayListOf()
        post.postLikes = arrayListOf()
        CoroutineScope(Dispatchers.IO).launch{
            PostsRepository().addPost(post).collect{ state->
                when(state){
                    is State.Success ->{

                    }
                }
            }
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
