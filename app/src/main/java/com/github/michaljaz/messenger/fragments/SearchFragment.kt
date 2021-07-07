package com.github.michaljaz.messenger.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.michaljaz.messenger.activities.MainActivity
import com.github.michaljaz.messenger.R
import com.google.android.material.textfield.TextInputEditText

class SearchFragment : Fragment() {
    private lateinit var m: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_search, container, false)
        m = activity as MainActivity

        //allow to go back
        m.allowBack=true

        //hide action bar
        m.supportActionBar!!.hide()

        //disable drawer
        m.disableDrawer()

        //request search focus
        if(m.searchKeyboard){
            view.findViewById<TextInputEditText>(R.id.Search).isFocusableInTouchMode = true
            view.findViewById<TextInputEditText>(R.id.Search).requestFocus()
            m.showKeyboard()
            m.searchKeyboard=false
        }


        //back arrow
        view.findViewById<ImageView>(R.id.imageView3).setOnClickListener {
            findNavController().navigate(R.id.search_off)
            m.hideKeyboard(view)
        }
        return view
    }
}
