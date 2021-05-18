package com.example.myapplication

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URI

class MyService : Service() {

    private val tag = "MyService"

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        showLog("onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showLog("onStartCommand")

        val runnable = Runnable {
            val socket = IO.socket("https://mess-serv.glitch.me")
            socket.connect()
            socket.on(Socket.EVENT_CONNECT) {
                showLog("connected")
            }
            socket.on(Socket.EVENT_DISCONNECT) {
                showLog("disconnected")
            }
            for (i in 1..10) {
                showLog("Service doing something.$i")
                Thread.sleep(1000)
            }
        }

        val thread = Thread(runnable)
        thread.start()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        showLog("onDestroy")
        super.onDestroy()
    }

    private fun showLog(message: String){
        Log.d(tag,message)
    }
}