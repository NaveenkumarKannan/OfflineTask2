package com.naveenkumar.offlinetask2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.naveenkumar.offlinetask2.R
import com.naveenkumar.offlinetask2.adapters.RecyclerAdapter
import com.naveenkumar.offlinetask2.utils.Utility

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var recyclerAdapter: RecyclerAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        initRecyclerView(root)
        addDataSet()

        return root
    }

    private fun addDataSet() {
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            recyclerAdapter.submitList(it)
            Utility.log("HomeViewModel")
            Utility.log(it.joinToString())
        })
    }

    private fun initRecyclerView(view: View) {
        view.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            recyclerAdapter = RecyclerAdapter()
            adapter = recyclerAdapter
        }
    }
}