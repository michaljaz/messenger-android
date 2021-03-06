package com.github.michaljaz.messenger.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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

        //disable drawer
        m.disableDrawer()

        //request search focus
        view.findViewById<EditText>(R.id.Search).requestFocus()
        view.findViewById<EditText>(R.id.Search).isFocusableInTouchMode = true
        m.showKeyboard()


        //back arrow
        view.findViewById<ImageView>(R.id.imageView3).setOnClickListener {
            findNavController().navigate(R.id.search_off)
            m.hideKeyboard(view)
        }
        return view
    }
}