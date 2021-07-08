package com.github.michaljaz.messenger.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.utils.RoundedTransformation
import com.squareup.picasso.Picasso


class MessagesAdapter(
    private val context: Context,
    private val texts: ArrayList<String>,
    private val isMe: ArrayList<Boolean>,
    private val friendPhotoUrl: String
) : BaseAdapter() {

    override fun getCount(): Int {
        return texts.size
    }

    override fun getItem(arg0: Int): Nothing? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row: View
        if(isMe[position]){
            val up = if(position==0){
                false
            }else{
                isMe[position-1]
            }
            val down=if(position==isMe.lastIndex){
                false
            }else{
                isMe[position+1]
            }
            row = inflater.inflate(R.layout.row_message_my, parent, false)
            val message=row.findViewById<TextView>(R.id.message)
            message.text=texts[position]
            if(up && down){
                message.setBackgroundResource(R.drawable.my_bubble_shape_middle)
            }
            if(up && !down){
                message.setBackgroundResource(R.drawable.my_bubble_shape_up)
            }
            if(!up && down){
                message.setBackgroundResource(R.drawable.my_bubble_shape_down)
            }
            if(!up && !down){
                message.setBackgroundResource(R.drawable.my_bubble_shape)
            }
        }else{
            val up = if(position==0){
                false
            }else{
                !isMe[position-1]
            }
            val down=if(position==isMe.lastIndex){
                false
            }else{
                !isMe[position+1]
            }
            row = inflater.inflate(R.layout.row_message_friend, parent, false)
            val message=row.findViewById<TextView>(R.id.message)
            message.text=texts[position]
            val i = row.findViewById<ImageView>(R.id.imgIcon)
            if(!(position>0 && !isMe[position-1])){
                if(friendPhotoUrl=="default"){
                    i.setImageResource(R.drawable.ic_profile_user)
                }else{
                    Picasso
                        .get()
                        .load(friendPhotoUrl)
                        .transform(RoundedTransformation(100, 0))
                        .into(i)
                }
            }
            if(up && down){
                message.setBackgroundResource(R.drawable.friend_bubble_shape_middle)
            }
            if(up && !down){
                message.setBackgroundResource(R.drawable.friend_bubble_shape_up)
            }
            if(!up && down){
                message.setBackgroundResource(R.drawable.friend_bubble_shape_down)
            }
            if(!up && !down){
                message.setBackgroundResource(R.drawable.friend_bubble_shape)
            }


        }
        return row
    }

}