package com.github.michaljaz.messenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

//private const val URL = "http://192.168.1.14:8080"
private const val URL = "https://mess-serv.glitch.me"

class MainActivity : AppCompatActivity() {
    private var socket: Socket? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        setSupportActionBar(findViewById(R.id.toolbar))
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun showLog(message: String){
        Log.d("lul",message)
    }
}
