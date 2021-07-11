package com.github.michaljaz.messenger.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.utils.RoundedTransformation
import com.squareup.picasso.Picasso

class Option(
    val icon: Int,
    val text: String)

class OptionsAdapter(
    private val context: Context,
    private val mOptions: ArrayList<Option>,
) : BaseAdapter() {

    override fun getCount(): Int {
        return mOptions.size
    }

    override fun getItem(position: Int): Option {
        return mOptions[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row: View = inflater.inflate(R.layout.row_option, parent, false)
        val i1: ImageView = row.findViewById(R.id.imgIcon) as ImageView
        val title: TextView = row.findViewById(R.id.txtTitle)
        title.text = mOptions[position].text
        i1.setImageResource(mOptions[position].icon)
        return row
    }

}