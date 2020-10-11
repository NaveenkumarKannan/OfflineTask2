package com.naveenkumar.offlinetask2.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.naveenkumar.offlinetask2.R
import com.naveenkumar.offlinetask2.model.Data

class HomeViewModel : ViewModel() {

    private val listData = MutableLiveData<List<Data>>().apply {
        val list = ArrayList<Data>()
        list.add(
            Data(
                "Planet Earth",
                "Alpha Studios",
                21,
                "Yesterday",
                R.color.violet,
                mutableListOf(
                    "https://api.androidhive.info/json/movies/2.jpg",
                    "https://api.androidhive.info/json/movies/1.jpg",
                    "https://api.androidhive.info/json/movies/3.jpg",

                    "https://api.androidhive.info/json/movies/4.jpg",
                    "https://api.androidhive.info/json/movies/5.jpg"
                )
            )
        )
        list.add(
            Data(
                "Demo Project",
                "Alpha Studios",
                12,
                "Yesterday",
                R.color.green,
                mutableListOf(
                    "https://api.androidhive.info/json/movies/4.jpg",
                    "https://api.androidhive.info/json/movies/5.jpg",
                    "https://api.androidhive.info/json/movies/6.jpg",

                    "https://api.androidhive.info/json/movies/4.jpg",
                    "https://api.androidhive.info/json/movies/4.jpg",
                    "https://api.androidhive.info/json/movies/4.jpg",
                    "https://api.androidhive.info/json/movies/4.jpg",
                    "https://api.androidhive.info/json/movies/4.jpg",

                    "https://api.androidhive.info/json/movies/4.jpg",
                    "https://api.androidhive.info/json/movies/4.jpg",
                    "https://api.androidhive.info/json/movies/4.jpg",
                    "https://api.androidhive.info/json/movies/5.jpg"
                )
            )
        )
        list.add(
            Data(
                "Frame.io movie",
                "Alpha Studios",
                15,
                "Yesterday",
                R.color.blue,
                mutableListOf(
                    "https://api.androidhive.info/json/movies/13.jpg",
                    "https://api.androidhive.info/json/movies/11.jpg"
                )
            )
        )
        list.add(
            Data(
                "Demo project",
                "Alpha Studios",
                18,
                "Yesterday",
                R.color.orange,
                mutableListOf(

                    "https://api.androidhive.info/json/movies/10.jpg",
                    "https://api.androidhive.info/json/movies/7.jpg",
                    "https://api.androidhive.info/json/movies/12.jpg",

                    "https://api.androidhive.info/json/movies/4.jpg",
                    "https://api.androidhive.info/json/movies/5.jpg",
                    "https://api.androidhive.info/json/movies/6.jpg",
                    "https://api.androidhive.info/json/movies/7.jpg",
                    "https://api.androidhive.info/json/movies/8.jpg",

                    "https://api.androidhive.info/json/movies/9.jpg",
                    "https://api.androidhive.info/json/movies/10.jpg",
                    "https://api.androidhive.info/json/movies/11.jpg",
                    "https://api.androidhive.info/json/movies/12.jpg",
                    "https://api.androidhive.info/json/movies/13.jpg",

                    "https://api.androidhive.info/json/movies/14.jpg",
                    "https://api.androidhive.info/json/movies/15.jpg"
                )
            )
        )
        list.add(
            Data(
                "Hollywood movies",
                "Alpha Studios",
                10,
                "Yesterday",
                R.color.red,
                mutableListOf(
                    "https://api.androidhive.info/json/movies/8.jpg",
                    "https://api.androidhive.info/json/movies/14.jpg",
                    "https://api.androidhive.info/json/movies/15.jpg",

                    "https://api.androidhive.info/json/movies/4.jpg",
                    "https://api.androidhive.info/json/movies/5.jpg"
                )
            )
        )
        list.add(
            Data(
                "Project 24",
                "Alpha Studios",
                24,
                "Yesterday",
                R.color.pink,
                mutableListOf(
                    "https://api.androidhive.info/json/movies/9.jpg",
                    "https://api.androidhive.info/json/movies/1.jpg",
                    "https://api.androidhive.info/json/movies/11.jpg",

                    "https://api.androidhive.info/json/movies/3.jpg",
                    "https://api.androidhive.info/json/movies/12.jpg",
                    "https://api.androidhive.info/json/movies/4.jpg",
                    "https://api.androidhive.info/json/movies/5.jpg"
                )
            )
        )
        value = list
    }
    val text: LiveData<List<Data>> = listData
}