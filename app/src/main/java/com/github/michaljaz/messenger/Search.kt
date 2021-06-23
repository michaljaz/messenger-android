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
        val view=inflater.inflate(R.layout.search, container, false)
        m = activity as MainActivity
        m.allowBack=true

        //hide action bar
        m.supportActionBar!!.hide()

        return view
    }
}
