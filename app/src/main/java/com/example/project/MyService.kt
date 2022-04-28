package com.example.project

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.project.R
import com.example.primera.menu.boolNotify
import com.google.firebase.database.*
import java.util.ArrayList

class MyService: Service() {

    private var dbref: DatabaseReference? = null
    private val listBool = ArrayList<boolNotify?>()
    private val TAG = "BackgroundSoundService"
    private val CHANNEL_ID = "channelTest"
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    lateinit var builderState: Notification.Builder

    override fun onBind(arg0: Intent?): IBinder? {
        Log.i(TAG, "onBind()")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate() , service started...")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val number = "1"
        dbref = FirebaseDatabase.getInstance().getReference("boolNotify")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        listBool.clear()
        dbref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (caseSnapshot in snapshot.children) {
                        val boolNoti = caseSnapshot.getValue(boolNotify::class.java)
                        listBool.add(boolNoti)
                    }
                    if (listBool[0]!!.boolNoti) {
                        val contentBool = boolNotify(number, false, "", "")
                        dbref!!.child(number).setValue(contentBool).addOnSuccessListener {
                            val notificationIntent = Intent(
                                applicationContext,
                                MainActivity::class.java
                            ).apply {
                                putExtra("selectNoti", "selectNoti")
                            }
                            val pendingIntent = PendingIntent.getActivity(
                                applicationContext,
                                0, notificationIntent, 0
                            )
                            createNotificationChannel()
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                notificationChannel = NotificationChannel("chaneltest", "channel defined", NotificationManager.IMPORTANCE_HIGH)
                                notificationChannel.enableLights(true)
                                notificationChannel.lightColor = Color.GREEN
                                notificationChannel.enableVibration(false)
                                notificationManager.createNotificationChannel(notificationChannel)

                                builder = Notification.Builder(applicationContext, "chaneltest")
                                    .setContentTitle("Se acaba se subir nuevo contenido de " + listBool[0]!!.type.lowercase())
                                    .setContentText(listBool[0]!!.title)
                                    .setSmallIcon(R.drawable.ic_baseline_person_24)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)

                                builderState = Notification.Builder(applicationContext, "chaneltest")
                                    .setContentTitle("Aplicación x")
                                    .setContentText("Aplicación x esta actualmente activa")
                                    .setSmallIcon(R.drawable.ic_baseline_settings_24)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)
                            } else {

                                builder = Notification.Builder(applicationContext)
                                    .setContentTitle("Se acaba se subir un " + listBool[0]!!.type)
                                    .setContentText(listBool[0]!!.title)
                                    .setSmallIcon(R.drawable.ic_baseline_person_24)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)

                                builderState = Notification.Builder(applicationContext)
                                    .setContentTitle("Aplicación x")
                                    .setContentText("Aplicación x esta actualmente activa")
                                    .setSmallIcon(R.drawable.ic_baseline_settings_24)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)
                            }

                            notificationManager.notify(1234, builder.build())
                            //startForeground(1, builderState.build())
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return START_STICKY
    }
    

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "chaneltest"
            val description = "test"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }


    fun onUnBind(arg0: Intent?): IBinder? {
        Log.i(TAG, "onUnBind()")
        return null
    }

    fun onStop() {
        Log.i(TAG, "onStop()")
    }

    fun onPause() {
        Log.i(TAG, "onPause()")
    }

    override fun onDestroy() {
        Log.i(TAG, "onCreate() , service stopped...")
    }

    override fun onLowMemory() {
        Log.i(TAG, "onLowMemory()")
    }
}