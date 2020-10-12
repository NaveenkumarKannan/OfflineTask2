package com.naveenkumar.offlinetask2.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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

        recyclerView = root.findViewById(R.id.recycler_view)
        srl = root.findViewById(R.id.srl)
        shimmerFrameLayout = root.findViewById(R.id.shimmer_frame_layout)
        init(root)

        return root
    }

    private fun init(root: View) {
        initRecyclerView(root)
        addDataSet()
    }

    private fun addDataSet() {
        homeViewModel.liveData.observe(viewLifecycleOwner, Observer {
            Handler(Looper.myLooper()!!).postDelayed({
                recyclerAdapter.submitList(it)
                Utility.close()
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }, 3 * 1000.toLong())
        })
    }

    private fun initRecyclerView(view: View) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            recyclerAdapter = RecyclerAdapter()
            adapter = recyclerAdapter
        }
        srl.setProgressBackgroundColorSchemeResource(R.color.swipe_color1)
        srl.setColorSchemeColors(view.context.resources.getColor(R.color.swipe_color2))
        srl.setOnRefreshListener {
            srl.isRefreshing = false
            init(view)
        }
        Utility.showProgress(view.context)
        shimmerFrameLayout.startShimmer()
        shimmerFrameLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

    }
}