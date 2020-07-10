package com.c0d3in3.finalproject.ui.dashboard.stories

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.bean.StoryModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_story_view.*
import java.lang.reflect.Type


class StoryViewActivity : AppCompatActivity(), StoryViewPagerAdapter.ViewPagerInterface{
    private lateinit var storiesList: ArrayList<ArrayList<StoryModel>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_view)

        val stories = intent.getStringExtra("storiesList")
        val position = intent.getIntExtra("position", 0)
        val listType: Type = object : TypeToken<ArrayList<ArrayList<StoryModel?>>?>() {}.type
        storiesList = Gson().fromJson(stories, listType)

        storyViewPager.adapter = StoryViewPagerAdapter(this, storiesList, this, position)
        storyViewPager.setCurrentItem(position, false)

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
