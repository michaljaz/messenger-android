package com.github.michaljaz.messenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

//private const val URL = "http://192.168.1.14:8080"
private const val URL = "https://mess-serv.glitch.me"

class MainActivity : AppCompatActivity() {
    private var socket: Socket? = null
    private lateinit var mNavDrawer:DrawerLayout
    private lateinit var toggle:ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        mNavDrawer=findViewById<DrawerLayout>(R.id.drawer_layout)

        toggle=ActionBarDrawerToggle(
            this,mNavDrawer,toolbar,R.string.app_name,R.string.nav_app_bar_open_drawer_description
        )
        mNavDrawer.addDrawerListener(toggle)
        disableDrawer()
        toggle.syncState()

        try {
            socket=IO.socket(URL)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
        socket?.connect()
        socket?.on(Socket.EVENT_CONNECT) {
            showLog("connected")
        }
        socket?.on(Socket.EVENT_DISCONNECT) {
            showLog("disconnected")
        }

        val intent = Intent(this,MyService::class.java)
        startService(intent)

    }

    fun getSocket(): Socket? {
        return socket
    }

    fun enableDrawer() {
        toggle.isDrawerIndicatorEnabled = true
        mNavDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    fun disableDrawer() {
        toggle.isDrawerIndicatorEnabled = false
        mNavDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }

    private fun showLog(message: String){
        Log.d("lul",message)
    }
}
