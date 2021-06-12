package com.github.michaljaz.messenger

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var mNavDrawer:DrawerLayout
    private lateinit var toggle:ActionBarDrawerToggle
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference

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
        showLog("disabled back press")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }

    private fun showLog(message: String){
        Log.d("lul",message)
    }
}
