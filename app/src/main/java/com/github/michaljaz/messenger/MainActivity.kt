package com.github.michaljaz.messenger

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL



class MainActivity : AppCompatActivity() {
    private lateinit var mNavDrawer:DrawerLayout
    private lateinit var toggle:ActionBarDrawerToggle
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference

    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            Log.e("src", src!!)
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.getInputStream()
            val myBitmap = BitmapFactory.decodeStream(input)
            Log.e("Bitmap", "returned")
            myBitmap
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main)
        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        auth=FirebaseAuth.getInstance()
        db = Firebase.database.reference

        mNavDrawer=findViewById(R.id.drawer_layout)
        toggle=ActionBarDrawerToggle(
            this,mNavDrawer,toolbar,R.string.app_name,R.string.nav_app_bar_open_drawer_description
        )
        mNavDrawer.addDrawerListener(toggle)
        disableDrawer()
        toggle.syncState()

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
            Picasso
                .get()
                .load(photoUrl)
                .into(navView.getHeaderView(0).findViewById<ImageView>(R.id.imageView));
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

    fun getFirebase(): FirebaseAuth {
        return auth
    }

    fun getFirebaseDatabase(): DatabaseReference{
        return db
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

    override fun onBackPressed() {
        Log.d("xd","disabled back press")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }
}
