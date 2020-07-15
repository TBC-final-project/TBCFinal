package com.c0d3in3.finalproject.ui.dashboard

import android.content.Intent
import android.content.pm.PackageManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.base.BaseActivity
import com.c0d3in3.finalproject.base.BasePagerAdapter
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.bean.StoryModel
import com.c0d3in3.finalproject.bean.UserModel
import com.c0d3in3.finalproject.image_chooser.EasyImage
import com.c0d3in3.finalproject.image_chooser.ImageChooserUtils
import com.c0d3in3.finalproject.image_chooser.MediaFile
import com.c0d3in3.finalproject.image_chooser.MediaSource
import com.c0d3in3.finalproject.network.FirebaseHandler
import com.c0d3in3.finalproject.ui.dashboard.home.HomeFragment
import com.c0d3in3.finalproject.ui.dashboard.notifications.NotificationsFragment
import com.c0d3in3.finalproject.ui.dashboard.search.SearchFragment
import com.c0d3in3.finalproject.ui.dashboard.stories.StoriesFragment
import com.c0d3in3.finalproject.ui.dashboard.stories.add_story.AddStoryActivity
import com.c0d3in3.finalproject.ui.post.create_post.CreatePostActivity
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_create_post_image.view.*

class DashboardActivity : BaseActivity() {

    private lateinit var adapter: BasePagerAdapter

    private var currentTitle = "news feed"

    private var imageFile: MediaFile? = null

    override fun getLayout() = R.layout.activity_dashboard

    override fun init() {

        setViewPager()

        initMainToolbar(currentTitle)

        addPostButton.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
            //addPosts()
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

    fun addStory(){
        ImageChooserUtils.choosePhoto(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ImageChooserUtils.PERMISSIONS_REQUEST) {
            if (grantResults.size > 2) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    ImageChooserUtils.chooseResource(this)
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

    fun sendStoryList(list: ArrayList<ArrayList<StoryModel>>){
        val frag = adapter.getItem(1) as StoriesFragment
        frag.setStoryList(list)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        ImageChooserUtils.easyImage.handleActivityResult(
            requestCode,
            resultCode,
            data,
            this,
            object : EasyImage.Callbacks {
                override fun onImagePickerError(error: Throwable, source: MediaSource) {

                }

                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    if (imageFiles.isNotEmpty()) {
                        imageFile = imageFiles[0]
                        val intent = Intent(this@DashboardActivity, AddStoryActivity::class.java)
                        intent.putExtra("imageUri", imageFile!!.uri.toString())
                        startActivity(intent)
                    }
                }

                override fun onCanceled(source: MediaSource) {

                }
            })
        super.onActivityResult(requestCode, resultCode, data)
    }
}