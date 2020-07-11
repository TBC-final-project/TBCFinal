package com.c0d3in3.finalproject.ui.dashboard

import androidx.viewpager.widget.ViewPager
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.base.BasePagerAdapter
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.bean.StoryModel
import com.c0d3in3.finalproject.bean.UserModel
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.UserInfo
import com.c0d3in3.finalproject.ui.dashboard.home.HomeFragment
import com.c0d3in3.finalproject.ui.dashboard.notifications.NotificationsFragment
import com.c0d3in3.finalproject.ui.dashboard.search.SearchFragment
import com.c0d3in3.finalproject.ui.dashboard.stories.StoriesFragment
import kotlinx.android.synthetic.main.activity_dashboard.*

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
//        val post = PostModel()
//        post.postAuthor = UserInfo.userInfo.userId
//        post.postTimestamp = System.currentTimeMillis()
//        post.postComments = arrayListOf()
//        post.postLikes = arrayListOf()
//        CoroutineScope(Dispatchers.IO).launch {
//            PostsRepository().addPost(post).collect {
//            }
//        }
        val userModel = UserModel()
        userModel.userId = (0..10000).random().toString()
        userModel.userFullName = "ted ${userModel.userId}"
        userModel.userFullNameToLowerCase = "ted ${userModel.userId}"
        userModel.userProfileImage = "https://firebasestorage.googleapis.com/v0/b/postit-tbc.appspot.com/o/user_profile_pictures%2F160f636c40c160cf9e37ec0d7a67a807.jpg?alt=media"
        FirebaseHandler.getDatabase().collection("users").document(userModel.userId).set(userModel)
        val storyModel = StoryModel()
        storyModel.storyAuthorId = userModel.userId
        storyModel.storyCreatedAt = System.currentTimeMillis()
        storyModel.storyValidUntil = storyModel.storyCreatedAt+10000000
        val mStoriesCollection = FirebaseHandler.getDatabase().collection("${FirebaseHandler.USERS_REF}/${userModel.userId}/${FirebaseHandler.STORIES_REF}")
        mStoriesCollection.add(storyModel)
        UserInfo.userInfo.userFollowing?.add(userModel.userId)
        FirebaseHandler.getDatabase().collection("users").document(UserInfo.userInfo.userId).update("userFollowing", UserInfo.userInfo.userFollowing)
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

    fun sendStoryList(list: ArrayList<ArrayList<StoryModel>>){
        val frag = adapter.getItem(1) as StoriesFragment
        frag.setStoryList(list)
    }
}