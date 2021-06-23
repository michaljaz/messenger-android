package com.github.michaljaz.messenger

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso


class CustomAdapter(context: Context, text1: ArrayList<String>, imageIds: ArrayList<String>) : BaseAdapter() {
    private val mContext: Context = context
    private val Title: ArrayList<String> = text1
    private val imge: ArrayList<String> = imageIds

    override fun getCount(): Int {
        // TODO Auto-generated method stub
        return Title.size
    }

    override fun getItem(arg0: Int): Nothing? {
        // TODO Auto-generated method stub
        return null
    }

    override fun getItemId(position: Int): Long {
        // TODO Auto-generated method stub
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row: View = inflater.inflate(R.layout.row, parent, false)
        val i1: ImageView = row.findViewById(R.id.imgIcon) as ImageView
        val title: TextView = row.findViewById(R.id.txtTitle)
        title.text = Title[position]
        Picasso
            .get()
            .load(imge[position])
            .transform(RoundedTransformation(100, 0))
            .into(i1)
        return row
    }

}