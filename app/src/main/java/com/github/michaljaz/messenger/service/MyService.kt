package com.github.michaljaz.messenger.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast

class MyService : Service() {

    private val tag = "MyService"

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    override fun onCreate() {
        showLog("onCreate")
        super.onCreate()
    }
    override fun onTaskRemoved(rootIntent: Intent?) {
        Toast.makeText(this, "Logged in", Toast.LENGTH_LONG).show()
        showLog("onTaskRemoved called")
        super.onTaskRemoved(rootIntent)
        this.stopSelf()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showLog("onStartCommand")

        val runnable = Runnable {

            while(true) {
//                showLog("Service is running.")
                Thread.sleep(1000)
            }
        }

        val thread = Thread(runnable)
        thread.start()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        showLog("onDestroy")
    }

    private fun showLog(message: String){
        Log.d(tag,message)
    }
}
