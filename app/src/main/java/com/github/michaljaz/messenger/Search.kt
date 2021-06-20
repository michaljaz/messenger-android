package com.github.michaljaz.messenger

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment

class Search : Fragment() {
    private lateinit var m: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.home_fragment, container, false)
        m = activity as MainActivity

        m.supportActionBar!!.hide()

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu);

        super.onCreateOptionsMenu(menu, inflater)
    }
}
