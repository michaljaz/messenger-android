package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import io.socket.client.IO
import io.socket.client.Socket

class MainActivity : AppCompatActivity() {
    val socket: Socket = IO.socket("https://mess-serv.glitch.me")
//    val socket: Socket = IO.socket("https://181d82419eb2.ngrok.io")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        setSupportActionBar(findViewById(R.id.toolbar))
        socket.connect()
        socket.on(Socket.EVENT_CONNECT) {
            showLog("connected")
        }
        socket.on(Socket.EVENT_DISCONNECT) {
            showLog("disconnected")
        }

        val intent = Intent(this,MyService::class.java)
        startService(intent)

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