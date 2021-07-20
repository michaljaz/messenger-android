package com.github.michaljaz.messenger.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.utils.RoundedTransformation
import com.squareup.picasso.Picasso

class Message(
    val text: String="",
    val isMe: Boolean=false,
    val messageTimestamp:Long=0,
    val isDate: Boolean=false,
    val dateString:String="")

class MessagesAdapter (private val mMessages: ArrayList<Message>,private val friendPhotoUrl: String,private val friendName: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    var MESSAGE_INTRO=0
    var MESSAGE_FRIEND=1
    var MESSAGE_MY=2
    var MESSAGE_DATE=3
    var onFriendLongClick: ((Message,TextView)->Unit) ?= null
    var onMyLongClick: ((Message,TextView)->Unit) ?= null

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message:TextView=itemView.findViewById(R.id.message)
        val icon:ImageView=itemView.findViewById(R.id.imgIcon)
        init {
            message.setOnLongClickListener {
                onFriendLongClick?.invoke(mMessages[adapterPosition],message)
                true
            }
        }
    }
    open inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message:TextView=itemView.findViewById(R.id.message)
        init {
            message.setOnLongClickListener {
                onMyLongClick?.invoke(mMessages[adapterPosition],message)
                true
            }
        }
    }
    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date:TextView=itemView.findViewById(R.id.date)
    }
    inner class IntroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val icon:ImageView=itemView.findViewById(R.id.intro_icon)
        val name:TextView=itemView.findViewById(R.id.intro_name)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            MESSAGE_INTRO -> {
                val v=inflater.inflate(R.layout.row_chat_intro, parent, false)
                IntroViewHolder(v)
            }
            MESSAGE_MY -> {
                val v=inflater.inflate(R.layout.row_message_my, parent, false)
                MyViewHolder(v)
            }
            MESSAGE_FRIEND -> {
                val v=inflater.inflate(R.layout.row_message_friend, parent, false)
                FriendViewHolder(v)
            }
            else -> {
                val v=inflater.inflate(R.layout.row_date, parent, false)
                DateViewHolder(v)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position==0 -> { MESSAGE_INTRO }
            mMessages[position].isDate -> { MESSAGE_DATE }
            mMessages[position].isMe -> { MESSAGE_MY }
            else -> { MESSAGE_FRIEND }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if(viewHolder is MyViewHolder) {
            val message: Message = mMessages[position]
            viewHolder.message.text=message.text

            val up=if(position==1 || mMessages[position-1].isDate){false}else{mMessages[position-1].isMe}
            val down=if(position==mMessages.lastIndex || mMessages[position+1].isDate){false}else{mMessages[position+1].isMe}

            if(up && down){
                viewHolder.message.setBackgroundResource(R.drawable.my_bubble_shape_middle)
            }
            if(up && !down){
                viewHolder.message.setBackgroundResource(R.drawable.my_bubble_shape_up)
            }
            if(!up && down){
                viewHolder.message.setBackgroundResource(R.drawable.my_bubble_shape_down)
            }
            if(!up && !down){
                viewHolder.message.setBackgroundResource(R.drawable.my_bubble_shape)
            }
        }else if(viewHolder is FriendViewHolder){
            val message: Message = mMessages[position]
            viewHolder.message.text=message.text

            val up=if(position==1 || mMessages[position-1].isDate){false}else{!mMessages[position-1].isMe}
            val down=if(position==mMessages.lastIndex || mMessages[position+1].isDate){false}else{!mMessages[position+1].isMe}

            if(up && down){
                viewHolder.message.setBackgroundResource(R.drawable.friend_bubble_shape_middle)
            }
            if(up && !down){
                viewHolder.message.setBackgroundResource(R.drawable.friend_bubble_shape_up)
            }
            if(!up && down){
                viewHolder.message.setBackgroundResource(R.drawable.friend_bubble_shape_down)
            }
            if(!up && !down){
                viewHolder.message.setBackgroundResource(R.drawable.friend_bubble_shape)
            }

            if(!down){
                if(friendPhotoUrl=="default"){
                    viewHolder.icon.setImageResource(R.drawable.ic_profile_user)
                }else{
                    Picasso
                        .get()
                        .load(friendPhotoUrl)
                        .transform(RoundedTransformation(100, 0))
                        .into(viewHolder.icon)
                }
            }else{
                viewHolder.icon.setImageResource(0)
            }
        }else if(viewHolder is IntroViewHolder){
            if(friendPhotoUrl=="default"){
                viewHolder.icon.setImageResource(R.drawable.ic_profile_user)
            }else{
                Picasso
                    .get()
                    .load(friendPhotoUrl)
                    .transform(RoundedTransformation(100, 0))
                    .into(viewHolder.icon)
            }
            viewHolder.name.text=friendName
        }else if(viewHolder is DateViewHolder){
            viewHolder.date.text=mMessages[position].dateString
        }

    }

    override fun getItemCount(): Int {
        return mMessages.size
    }
}