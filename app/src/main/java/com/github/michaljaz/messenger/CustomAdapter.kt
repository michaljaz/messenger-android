package com.github.michaljaz.messenger

import android.content.Context
import android.icu.number.NumberFormatter.with
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso


class CustomAdapter(context: Context, text1: ArrayList<String>, imageIds: ArrayList<String>, userids: ArrayList<String>) : BaseAdapter() {
    private val mContext: Context = context
    private val Title: ArrayList<String> = text1
    private val imge: ArrayList<String> = imageIds
    private val userids: ArrayList<String> = userids

    override fun getCount(): Int {
        // TODO Auto-generated method stub
        return Title.size
    }

    override fun getItem(arg0: Int): ArrayList<String> {
        // TODO Auto-generated method stub
        var xd= ArrayList<String>()
        xd.add(userids[arg0])
        xd.add(Title[arg0])
        return xd
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
        var image=imge[position]
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