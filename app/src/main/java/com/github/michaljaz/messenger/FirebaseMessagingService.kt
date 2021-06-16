package com.github.michaljaz.messenger

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.FirebaseMessagingService

class FirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "FirebaseMessagingService"

    @SuppressLint("LongLogTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "msg received: ${remoteMessage.from}")
        if (remoteMessage.notification != null) {
            showNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
        }
    }
    override fun onNewToken(token: String) {
        Log.d("TAG", "Refreshed token: $token")
    }
    private fun showNotification(title: String?, body: String?) {
        Log.d("xd", title.toString())
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
//            PendingIntent.FLAG_ONE_SHOT)
//        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationBuilder = NotificationCompat.Builder(this)
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setContentTitle(title)
//            .setContentText(body)
//            .setAutoCancel(true)
//            .setSound(soundUri)
//            .setContentIntent(pendingIntent)
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(0, notificationBuilder.build())
    }
}