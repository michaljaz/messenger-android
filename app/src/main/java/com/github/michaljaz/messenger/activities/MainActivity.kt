package com.github.michaljaz.messenger.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.adapters.Chat
import com.github.michaljaz.messenger.adapters.Option
import com.github.michaljaz.messenger.adapters.OptionsAdapter
import com.github.michaljaz.messenger.fragments.HomeFragment
import com.github.michaljaz.messenger.service.MyService
import com.github.michaljaz.messenger.utils.RoundedTransformation
import com.github.michaljaz.messenger.utils.setIconUrl
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {
    lateinit var mNavDrawer:DrawerLayout
    lateinit var auth: FirebaseAuth
    lateinit var db: DatabaseReference
    lateinit var dialog:BottomSheetDialog
    private var statusDelay: Long=5000
    var chatWith: String="null"
    var chatWithUid: String="null"
    var chatWithPhoto: String="null"
    var allowBack: Boolean=false
    var lastTouch=0f


    override fun onPause() {
        dialog.cancel()
        super.onPause()
    }

    fun setCacheChats(chats: ArrayList<Chat>){
        val chatsJSON = Gson().toJson(chats)
        val prefs:SharedPreferences = getSharedPreferences("chats", Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor = prefs.edit()
        editor.putString("chats", chatsJSON)
        editor.apply()
    }

    fun getCacheChats(): ArrayList<Chat>? {
        val prefs: SharedPreferences = getSharedPreferences("chats", Context.MODE_PRIVATE)
        val chatsJSON: String? = prefs.getString("chats", "")
        return Gson().fromJson(chatsJSON, object : TypeToken<ArrayList<Chat?>?>() {}.type)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        lastTouch=event!!.y
        return super.dispatchTouchEvent(event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //dialog
        dialog = BottomSheetDialog(this)
        val v = layoutInflater.inflate(R.layout.sheet_bottom, null)
        val list = v.findViewById<ListView>(R.id.list)
        val options=ArrayList<Option>()
        options.add(Option(R.drawable.ic_archive_filled_box,"Archive"))
        options.add(Option(R.drawable.ic_delete,"Delete"))
        options.add(Option(R.drawable.ic_disable_alarm,"Mute notifications"))
        list.adapter=OptionsAdapter(this,options)
        dialog.setCancelable(true)
        dialog.setContentView(v)

        //firebase offline
        Firebase.database.setPersistenceEnabled(true)

        //firebase app check
        FirebaseApp.initializeApp(/*context=*/ this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance())

        //Setup firebase
        auth=FirebaseAuth.getInstance()
        db = Firebase.database.reference


        //Setup nav drawer
        mNavDrawer=findViewById(R.id.drawer_layout)
        disableDrawer()

        //Background service
        val intent = Intent(this, MyService::class.java)
        startService(intent)

        loop()
    }

    private fun loop(){
        if(auth.currentUser!=null){
            db.child("/usersData/${auth.currentUser!!.uid}/status").setValue(System.currentTimeMillis().toString())
        }
        val handler = Handler()
        handler.postDelayed( {
            loop()
        }, statusDelay)
    }

    fun getChatRef(userid:String): DatabaseReference? {
        return try{
            if(userid>auth.currentUser!!.uid){
                db.child("/chats/${auth.currentUser!!.uid}/${userid}")
            }else{
                db.child("/chats/${userid}/${auth.currentUser!!.uid}")
            }
        }catch(e:Exception){
            null
        }
    }

    fun sessionHelper(f: HomeFragment){
        var clicked=false
        findViewById<NavigationView>(R.id.side_navigation).setNavigationItemSelectedListener { item ->
            if(!clicked) {
                try {
                    when (item.itemId) {
                        R.id.page_1 -> {
                            clicked=true
                            auth.signOut()
                            f.logout()
                        }
                        R.id.page_2 -> {
                            clicked=true
                            if (auth.currentUser != null) {
                                db.child("users").child(auth.currentUser!!.uid).removeValue()
                                    .addOnCompleteListener {
                                        db.child("usersData").child(auth.currentUser!!.uid)
                                            .removeValue().addOnCompleteListener {
                                                auth.currentUser!!.delete()
                                                auth.signOut()
                                                f.logout()
                                            }
                                    }
                            }

                        }
                    }
                } catch (e: Exception) { }
            }
            true
        }
    }

    fun updateHeader(title:String,subtitle:String,photoUrl:String){
        try{
            val navView = findViewById<View>(R.id.side_navigation) as NavigationView
            navView.getHeaderView(0).findViewById<TextView>(R.id.title_name).text=title
            navView.getHeaderView(0).findViewById<TextView>(R.id.subtitle_name).text=subtitle
            navView.getHeaderView(0).findViewById<ImageView>(R.id.imageView).setIconUrl(photoUrl)

        }catch(e:Exception){}
    }

    fun enableDrawer() {
        mNavDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    fun disableDrawer() {
        mNavDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    fun hideKeyboard(v: View) {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)

    }

    fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    override fun onBackPressed() {
        if(allowBack){
            super.onBackPressed()
        }else{
            moveTaskToBack(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }
}
