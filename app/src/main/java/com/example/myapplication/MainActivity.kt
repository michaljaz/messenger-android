package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.socket.client.IO
import io.socket.client.Socket

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        setSupportActionBar(findViewById(R.id.toolbar))
        val socket = IO.socket("https://mess-serv.glitch.me")
        socket.connect()
        socket.on(Socket.EVENT_CONNECT) {
            showLog("connected")
        }
        socket.on(Socket.EVENT_DISCONNECT) {
            showLog("disconnected")
        }
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            findViewById<TextInputLayout>(R.id.Username).editText?.let {
                Snackbar.make(
                    view,
                    it.text,
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Action", null).show()
            }
        }

        val intent = Intent(this,MyService::class.java)
        startService(intent)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun showLog(message: String){
        Log.d("lul",message)
    }
}