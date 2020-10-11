package com.naveenkumar.offlinetask2.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.naveenkumar.offlinetask2.R
import com.naveenkumar.offlinetask2.adapters.RecyclerAdapter
import com.naveenkumar.offlinetask2.utils.Utility

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var srl: SwipeRefreshLayout
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

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
        homeViewModel.liveData.observe(viewLifecycleOwner, Observer {
            Handler(Looper.myLooper()!!).postDelayed({
                srl.isRefreshing = false
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                recyclerAdapter.submitList(it)
            }, 5 * 1000.toLong())
        })
    }

    private fun initRecyclerView(view: View) {

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            recyclerAdapter = RecyclerAdapter()
            adapter = recyclerAdapter
        }
        srl = view.findViewById(R.id.srl)
        srl.setOnRefreshListener {
            srl.isRefreshing = true
            shimmerFrameLayout.startShimmer()
            shimmerFrameLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            addDataSet()
        }
        shimmerFrameLayout = view.findViewById(R.id.shimmer_frame_layout)
        shimmerFrameLayout.startShimmer()
    }
}