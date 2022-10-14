package com.example.myweatherapp.notify

import android.Manifest
import android.app.*
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.myweatherapp.MainActivity
import com.example.myweatherapp.R
import com.example.myweatherapp.data.remote.WeatherApi
import com.example.myweatherapp.repository.WeatherRepository
import com.example.myweatherapp.ui.home.HomeViewModel
import com.example.myweatherapp.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ServiceCall : Service() {


    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val channelId = "200"
    var latlong:String =" "


    override fun onCreate() {
        super.onCreate()
        Log.d("ServiceTest","OnCreate")
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }



        mFusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
                Log.d("ServiceTest","Location : "+location?.latitude.toString())
                if (location != null) {
                    latlong = "Lat : "+location.latitude.toString() +"Lon : "+location.latitude.toString()

                    // Network Call For Location


                    val notif = this?.let {
                        NotificationCompat.Builder(this,"200")
                            .setContentTitle("Weather App")
                            .setContentText("Today's Temprature : 19 C")
                            .setSmallIcon(R.drawable.icn_few_clouds)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .build()
                    }


                    val notifManger = this?.let { NotificationManagerCompat.from(this) }
                    if (notifManger != null) {
                        if (notif != null) {
                            notifManger.notify(1252,notif)
                        }
                    }
                }

            }


    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}