package com.c0d3in3.finalproject.ui.dashboard.stories.story_view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.bean.StoryModel
import com.c0d3in3.finalproject.ui.dashboard.stories.story_view.StoryViewPagerAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_story_view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import kotlin.properties.Delegates


class StoryViewActivity : AppCompatActivity(), StoryViewPagerAdapter.ViewPagerInterface {
    private lateinit var storiesList: ArrayList<ArrayList<StoryModel>>
    private var currentPosition by Delegates.notNull<Int>()
    private lateinit var adapter: StoryViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_view)

        val stories = intent.getStringExtra("storiesList")
        currentPosition = intent.getIntExtra("position", 0)
        val listType: Type = object : TypeToken<ArrayList<ArrayList<StoryModel?>>?>() {}.type
        storiesList = Gson().fromJson(stories, listType)

        adapter = StoryViewPagerAdapter(
            this,
            storiesList,
            this)
        storyViewPager.adapter = adapter

        storyViewPager.currentItem = currentPosition
        adapter.defaultPosition = currentPosition


        storyViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                println(position)
                adapter.updateCurrentItem(position)
            }

        })
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            adapter.updateCurrentItem(currentPosition)
        }

    }

    override fun nextViewPagerItem() : Boolean{
        var result = false
        if(storyViewPager.currentItem+1 < storiesList.size){
            storyViewPager.currentItem = storyViewPager.currentItem +1
            result = true
        }
        else finish()
        println(result)
        return result
    }



    override fun previousViewPagerItem(): Boolean{
        var result = false
        if(storyViewPager.currentItem > 0) {
            storyViewPager.currentItem = storyViewPager.currentItem - 1
            result = true
        }
        return result
    }

}
