package com.github.michaljaz.messenger

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText

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

        //disable drawer
        m.disableDrawer()

        //request search focus
        view.findViewById<TextInputEditText>(R.id.Search).requestFocus()
        m.showKeyboard()

        //back arrow
        view.findViewById<ImageView>(R.id.imageView3).setOnClickListener {
            m.hideKeyboard(view)
            findNavController().navigate(R.id.search_off)
        }
        return view
    }
}