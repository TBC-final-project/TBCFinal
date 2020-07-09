package com.c0d3in3.finalproject.ui.dashboard

import androidx.viewpager.widget.ViewPager
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.base.BasePagerAdapter
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DashboardActivity : BaseActivity() {

    private lateinit var adapter: BasePagerAdapter

    private var currentTitle = "news feed"

    override fun getLayout() = R.layout.activity_dashboard

    override fun init() {

        setViewPager()

        initMainToolbar(currentTitle)

        addPostButton.setOnClickListener {
            addPosts()
        }

        addViewPagerListener()
        addNavMenuListener()
    }


    private fun addNavMenuListener() {
        nav_view.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navHome ->{
                    currentTitle = getString(R.string.news_feed)
                    dashboardPager.setCurrentItem(0, true)
                }
                R.id.navStories ->{
                    currentTitle = getString(R.string.stories)
                    dashboardPager.setCurrentItem(1, true)
                }
                R.id.navNotifications ->{
                    currentTitle = getString(R.string.notifications)
                    dashboardPager.setCurrentItem(2, true)
                }
                R.id.navSearch ->{
                    currentTitle = getString(R.string.search)
                    dashboardPager.setCurrentItem(3, true)
                }
            }
            setToolbarTitle(currentTitle)


            true
        }
    }

    private fun addPosts() {
        val post = PostModel()
        post.postAuthor = UserInfo.userInfo.userId
        post.postTimestamp = System.currentTimeMillis()
        post.postComments = arrayListOf()
        post.postLikes = arrayListOf()
        CoroutineScope(Dispatchers.IO).launch {
            PostsRepository().addPost(post).collect {
            }
        }
    }

    private fun addViewPagerListener() {
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


    private fun setViewPager() {
        adapter =
            BasePagerAdapter(supportFragmentManager)

        adapter.addFragment(HomeFragment())
        adapter.addFragment(StoriesFragment())
        adapter.addFragment(NotificationsFragment())
        adapter.addFragment(SearchFragment())

        dashboardPager.offscreenPageLimit = 4
        dashboardPager.adapter = adapter
    }
}