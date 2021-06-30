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


class CustomAdapter(
    private val context: Context,
    private val titles: ArrayList<String>,
    private val imageIds: ArrayList<String>,
    private val userids: ArrayList<String>
) : BaseAdapter() {

    override fun getCount(): Int {
        return titles.size
    }

    override fun getItem(arg0: Int): ArrayList<String> {
        val xd= ArrayList<String>()
        xd.add(userids[arg0])
        xd.add(titles[arg0])
        return xd
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row: View = inflater.inflate(R.layout.row, parent, false)
        val i1: ImageView = row.findViewById(R.id.imgIcon) as ImageView
        val title: TextView = row.findViewById(R.id.txtTitle)
        title.text = titles[position]
        val image=imageIds[position]
        if(image=="default"){
            i1.setImageResource(R.drawable.ic_profile_user)
        }else{
            Picasso
                .get()
                .load(image)
                .transform(RoundedTransformation(100, 0))
                .into(i1)
        }
        return row
    }

}