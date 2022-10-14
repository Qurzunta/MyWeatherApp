package com.example.myweatherapp.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReminderBroadCast: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val i  =  Intent(p0, ServiceCall::class.java)
        if (p0 != null) {
            p0.startService(Intent(p0, ServiceCall::class.java))
            //Log.d("ServiceTest","Initiated")
        }
    }
}