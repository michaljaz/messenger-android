package com.github.michaljaz.messenger

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation


class MainActivity : AppCompatActivity() {
    lateinit var mNavDrawer:DrawerLayout
    lateinit var toggle:ActionBarDrawerToggle
    lateinit var auth: FirebaseAuth
    lateinit var db: DatabaseReference
    var allowBack: Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main)

        //Custom toolbar
        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Setup firebase
        auth=FirebaseAuth.getInstance()
        db = Firebase.database.reference

        //Setup nav drawer
        mNavDrawer=findViewById(R.id.drawer_layout)
        toggle=ActionBarDrawerToggle(
            this,mNavDrawer,toolbar,R.string.app_name,R.string.nav_app_bar_open_drawer_description
        )
        mNavDrawer.addDrawerListener(toggle)
        disableDrawer()
        toggle.syncState()

        //Background service
        val intent = Intent(this,MyService::class.java)
        startService(intent)
    }

    fun sessionHelper(f:HomeFragment){
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
            if(photoUrl=="default"){
                navView.getHeaderView(0).findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.ic_profile_user)
            }else{
                Picasso
                    .get()
                    .load(photoUrl)
                    .transform(RoundedTransformation(100, 0))
                    .into(navView.getHeaderView(0).findViewById<ImageView>(R.id.imageView))
            }

        }catch(e:Exception){}
    }

    fun enableDrawer() {
        toggle.isDrawerIndicatorEnabled = true
        mNavDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    fun disableDrawer() {
        toggle.isDrawerIndicatorEnabled = false
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

    fun setToolbarTitle(title:String){
        supportActionBar?.title = title
    }

    override fun onBackPressed() {
        if(allowBack){
            super.onBackPressed()
        }else{
            Log.d("xd","disabled back press")
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }
}
