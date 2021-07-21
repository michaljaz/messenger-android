package com.github.michaljaz.messenger.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.utils.setIconUrl

class Message(
    val text: String="",
    val isMe: Boolean=false,
    val messageTimestamp:Long=0,
    val isDate: Boolean=false,
    val dateString:String="",
    val withTimeBreak:Boolean=false)

class MessagesAdapter (private val mMessages: ArrayList<Message>,private val friendPhotoUrl: String,private val friendName: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var MESSAGE_INTRO=0
    private var MESSAGE_FRIEND=1
    private var MESSAGE_MY=2
    private var MESSAGE_DATE=3

    var onFriendLongClick: ((Message,TextView)->Unit) ?= null
    var onMyLongClick: ((Message,TextView)->Unit) ?= null

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message:TextView=itemView.findViewById(R.id.message)
        val icon:ImageView=itemView.findViewById(R.id.imgIcon)
        val layout:LinearLayout=itemView.findViewById(R.id.layout)
        init {
            message.setOnClickListener {
                Log.d("xd","clicked")
                message.background.alpha=200
            }
            message.setOnLongClickListener {
                onFriendLongClick?.invoke(mMessages[adapterPosition],message)
                true
            }
        }
    }
    open inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message:TextView=itemView.findViewById(R.id.message)
        val layout:LinearLayout=itemView.findViewById(R.id.layout)
        init {
            message.setOnClickListener {
                Log.d("xd",mMessages[adapterPosition].withTimeBreak.toString())
                Log.d("xd","clicked")
                message.background.alpha=200
            }
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
        val message: Message = mMessages[position]
        if(viewHolder is MyViewHolder) {
            viewHolder.message.text=message.text

            val isMyUp=if(position==1 || mMessages[position-1].isDate || message.withTimeBreak){false}else{mMessages[position-1].isMe}
            val isMyDown=if(position==mMessages.lastIndex || mMessages[position+1].isDate || mMessages[position+1].withTimeBreak){false}else{mMessages[position+1].isMe}

            if(message.withTimeBreak){
                val param=viewHolder.layout.layoutParams as LinearLayout.LayoutParams
                param.setMargins(0,40,0,0)
                viewHolder.layout.layoutParams=param
            }else{
                val param=viewHolder.layout.layoutParams as LinearLayout.LayoutParams
                param.setMargins(0,0,0,0)
                viewHolder.layout.layoutParams=param
            }

            if(isMyUp){
                if(isMyDown){
                    viewHolder.message.setBackgroundResource(R.drawable.my_bubble_shape_middle)
                }else{
                    viewHolder.message.setBackgroundResource(R.drawable.my_bubble_shape_up)
                }
            }else{
                if(isMyDown){
                    viewHolder.message.setBackgroundResource(R.drawable.my_bubble_shape_down)
                }else{
                    viewHolder.message.setBackgroundResource(R.drawable.my_bubble_shape)
                }
            }




        }else if(viewHolder is FriendViewHolder){
            viewHolder.message.text=message.text

            val isFriendUp=if(position==1 || mMessages[position-1].isDate || message.withTimeBreak){false}else{!mMessages[position-1].isMe}
            val isFriendDown=if(position==mMessages.lastIndex || mMessages[position+1].isDate || mMessages[position+1].withTimeBreak){false}else{!mMessages[position+1].isMe}

            if(message.withTimeBreak){
                val param=viewHolder.layout.layoutParams as LinearLayout.LayoutParams
                param.setMargins(0,40,0,0)
                viewHolder.layout.layoutParams=param
            }else{
                val param=viewHolder.layout.layoutParams as LinearLayout.LayoutParams
                param.setMargins(0,0,0,0)
                viewHolder.layout.layoutParams=param
            }

            if(isFriendUp){
                if(isFriendDown){
                    viewHolder.message.setBackgroundResource(R.drawable.friend_bubble_shape_middle)
                }else{
                    viewHolder.message.setBackgroundResource(R.drawable.friend_bubble_shape_up)
                }
            }else{
                if(isFriendDown){
                    viewHolder.message.setBackgroundResource(R.drawable.friend_bubble_shape_down)
                }else{
                    viewHolder.message.setBackgroundResource(R.drawable.friend_bubble_shape)
                }
            }

            if(!isFriendDown){
                viewHolder.icon.setIconUrl(friendPhotoUrl)
            }else{
                viewHolder.icon.setImageResource(0)
            }

        }else if(viewHolder is IntroViewHolder){
            viewHolder.icon.setIconUrl(friendPhotoUrl)
            viewHolder.name.text=friendName
        }else if(viewHolder is DateViewHolder){
            viewHolder.date.text=mMessages[position].dateString
        }

    }

    override fun getItemCount(): Int {
        return mMessages.size
    }
}