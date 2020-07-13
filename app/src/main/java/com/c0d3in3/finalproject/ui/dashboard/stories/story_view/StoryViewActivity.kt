package com.c0d3in3.finalproject.ui.dashboard.stories.story_view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.R
import com.c0d3in3.finalproject.base.BasePagerAdapter
import com.c0d3in3.finalproject.bean.StoryModel
import com.c0d3in3.finalproject.bean.UserModel
import com.c0d3in3.finalproject.network.State
import com.c0d3in3.finalproject.network.StoriesRepository
import com.c0d3in3.finalproject.ui.dashboard.stories.StoriesViewModel
import com.c0d3in3.finalproject.ui.dashboard.stories.StoriesViewModelFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_story_view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.lang.reflect.Type
import kotlin.properties.Delegates


class StoryViewActivity : AppCompatActivity() {
    private lateinit var storiesList: ArrayList<ArrayList<StoryModel>>
    private var currentPosition by Delegates.notNull<Int>()
    private lateinit var adapter: BasePagerAdapter
    private lateinit var storyViewModel : StoriesViewModel
    private var isLoading = false

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
        if(storyViewPager.currentItem == storiesList.size-2 && storiesList.size >= 12){
            isLoading = true
            loadMoreStories()
        }
        else{
            if(storyViewPager.currentItem+1 < storiesList.size){
                storyViewPager.currentItem = storyViewPager.currentItem +1
                val frag = adapter.getItem(storyViewPager.currentItem) as StoryViewFragment
                frag.startTimer()
            }

            else finish()
        }

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

    private fun loadMoreStories(){
        CoroutineScope(Dispatchers.IO).launch{
            StoriesRepository().getAllStories(10, storiesList[storiesList.size-1][0].storyAuthorId).collect { state ->
                when (state) {

                    is State.Loading ->{

                    }

                    is State.Success -> {
                        withContext(Dispatchers.Main){
                            storyViewPager.currentItem = storyViewPager.currentItem +1
                            val frag = adapter.getItem(storyViewPager.currentItem) as StoryViewFragment
                            frag.startTimer()
                        }
                        isLoading = false
                        if(state.data.isNotEmpty()){
                            storiesList.addAll(state.data)
                            withContext(Dispatchers.Main){
                                state.data.forEach {
                                    adapter.addFragment(StoryViewFragment(it, false))
                                }
                                adapter.notifyDataSetChanged()
                            }
                        }

                    }

                    is State.Failed -> withContext(Dispatchers.Main) {
                        isLoading = false
                        Toast.makeText(
                            App.getInstance().applicationContext,
                            "Error while loading stories",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } // END when
            } // END collect
        }
    }

}
