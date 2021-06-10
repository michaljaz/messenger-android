package com.github.michaljaz.messenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout


class MainActivity : AppCompatActivity() {
    private lateinit var mNavDrawer:DrawerLayout
    private lateinit var toggle:ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
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
