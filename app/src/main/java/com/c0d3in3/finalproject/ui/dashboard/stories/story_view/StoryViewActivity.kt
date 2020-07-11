package com.c0d3in3.finalproject.ui.dashboard.stories.story_view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BasePagerAdapter
import com.c0d3in3.finalproject.bean.StoryModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_story_view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import kotlin.properties.Delegates


class StoryViewActivity : AppCompatActivity() {
    private lateinit var storiesList: ArrayList<ArrayList<StoryModel>>
    private var currentPosition by Delegates.notNull<Int>()
    private lateinit var adapter: BasePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_view)

        val stories = intent.getStringExtra("storiesList")
        currentPosition = intent.getIntExtra("position", 0)
        val listType: Type = object : TypeToken<ArrayList<ArrayList<StoryModel?>>?>() {}.type
        storiesList = Gson().fromJson(stories, listType)

        adapter = BasePagerAdapter(supportFragmentManager)

        storiesList.forEachIndexed { index, arrayList ->
            if(index == currentPosition) adapter.addFragment(StoryViewFragment(arrayList, true))
            else adapter.addFragment(StoryViewFragment(arrayList, false))
        }
        storyViewPager.adapter = adapter


        storyViewPager.currentItem = currentPosition

    }

    fun nextViewPagerItem(){
        if(storyViewPager.currentItem+1 < storiesList.size){
            storyViewPager.currentItem = storyViewPager.currentItem +1
            val frag = adapter.getItem(storyViewPager.currentItem) as StoryViewFragment
            frag.startTimer()
        }

        else finish()
    }

    fun previousViewPagerItem(){
        if(storyViewPager.currentItem == 1) finish()
        if(storyViewPager.currentItem > 0){
            storyViewPager.currentItem = storyViewPager.currentItem - 1
            val frag = adapter.getItem(storyViewPager.currentItem) as StoryViewFragment
            frag.startTimer()
        }
        else return
    }

}
