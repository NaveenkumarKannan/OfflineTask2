package com.naveenkumar.offlinetask2.ui.downloads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.naveenkumar.offlinetask2.utils.Utility
import com.naveenkumar.offlinetask2.R

class DownloadsFragment : Fragment() {

    private lateinit var downloadsViewModel: DownloadsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        downloadsViewModel =
                ViewModelProvider(this).get(DownloadsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_downloads, container, false)
        val textView: TextView = root.findViewById(R.id.text_downloads)
        downloadsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
            Utility.log(it)
        })
        return root
    }
}