package com.example.myweatherapp.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweatherapp.MainActivity
import com.example.myweatherapp.R
import com.example.myweatherapp.WeatherAppDatabase
import com.example.myweatherapp.dao.FavCitiesDao
import com.example.myweatherapp.data.models.FavCity
import com.example.myweatherapp.data.remote.Daily
import com.example.myweatherapp.databinding.FragmentHomeBinding
import com.example.myweatherapp.notify.ReminderBroadCast
import com.example.myweatherapp.utils.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class homeFragment : Fragment() {


    val CHANNEL_ID = "0092"
    val CHANNEL_NAME = "weatherApp"
    val NOTIF_ID = 0




    private lateinit var dao: FavCitiesDao

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HomeAdapter

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 233230

    private val homeViewModel by activityViewModels<HomeViewModel>()

    private lateinit var scaleType:String


    private lateinit var dayslist:List<Daily>

    private var mainTemp:Double = 0.0
    private var mainFeelsLike:Double = 0.0

    private var isFav:Boolean = false

    @Inject
    lateinit var scaleManager: ScaleManager

    lateinit var lat:String
    lateinit var lon:String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        // Initialize the DAO..
         dao = WeatherAppDatabase.getDatabase(requireContext()).favCitiesDao()
        scaleType = "C"

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        adapter = HomeAdapter()

        createNotifChannel()
        

        return binding.root

    }

    private fun createNotifChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }
            val manager = activity?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scaleType = scaleManager.getScale().toString()


        // Notification Code

        createNotificationsChannels()
        val intent1=Intent(activity, ReminderBroadCast::class.java)

        val pendingIntent1:PendingIntent = PendingIntent.getBroadcast(activity,0,intent1,0)
        val alarmM : AlarmManager = (activity?.getSystemService(ALARM_SERVICE) as AlarmManager?)!!

        //Testing Notification
        var tme = System.currentTimeMillis()
        var addit = 1000*10

        // Subscribing For Reminders


        alarmM.set(AlarmManager.RTC_WAKEUP,tme+addit,pendingIntent1,)




        // end


        // Initialize the SDK
        Places.initialize(activity?.applicationContext, Constants.API_PLACES_KEY)

        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        binding.myToolbar.inflateMenu(R.menu.mymenu)
        binding.myToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_SearchCity -> {

                    // Start the autocomplete intent.
                    val intent =
                        Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(context)
                    startActivityForResult(intent, 1)
                    true
                }

                R.id.action_ViewFavourite -> {
                    // User chose the "Favorite" action, mark the current item
                    // as a favorite...
                    findNavController().navigate(R.id.action_homeFragment_to_bookmarkedFragment)

                    true
                }
                R.id.action_ChanegUnit -> {
                    // User chose the "Favorite" action, mark the current item
                    // as a favorite...
                    changeUnit()


                    true
                }

                else -> {
                    // If we got here, the user's action was not recognized.
                    // Invoke the superclass to handle it.
                    super.onOptionsItemSelected(it)
                }
            }

        }

        binding.fhFav.setOnClickListener{
            if(isFav){
                binding.fhFav.setImageResource(R.drawable.icn_fav_empty)
                isFav = false
                Toast.makeText(context,"Removed from favourite city",Toast.LENGTH_SHORT).show()

                lifecycleScope.launch{
                 dao.deleteFavCity(binding.fhTvName.text as String)
                }

            }else{
                binding.fhFav.setImageResource(R.drawable.icn_fav_filled)
                isFav = true
                Toast.makeText(context,"Added to favourite city",Toast.LENGTH_SHORT).show()
                // send data to local DB
                val favcity = FavCity(binding.fhTvName.text as String,lat,lon)
                lifecycleScope.launch {
                    dao.insert(favcity)}
            }
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // the string you passed in .navigate()
        var mycity: String? = getArguments()?.getString("city");

        if(mycity!=null){
            var mylat: String? = getArguments()?.getString("lat");
            var mylon: String? = getArguments()?.getString("lon");
            homeViewModel.getWeather(
                "" + mylat,
                "" + mylon,
            )

            lat=""+mylat
            lon = ""+mylon
            binding.fhTvName.text = mycity
            // Hide Toolbar
            // to stop deadlock of navigation
            binding.myToolbar.visibility = View.INVISIBLE

        }else{
            getLocation()
            binding.myToolbar.visibility = View.VISIBLE
        }

        //Toast.makeText(activity,"Args : "+mycity,Toast.LENGTH_SHORT).show()


        binding.dailyList.layoutManager = LinearLayoutManager(activity)
        binding.dailyList.adapter = adapter
        bindObservers()
    }



    private fun createNotificationsChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "200",
                "weatherApp",
                NotificationManager.IMPORTANCE_HIGH
            )
            activity?.let {
                ContextCompat.getSystemService(it, NotificationManager::class.java)
                    ?.createNotificationChannel(channel)
            }
        }
    }

    private fun changeUnit(){

        if(scaleType=="C"){
            scaleType = "F"
            scaleManager.saveScale(scaleType)
            //UI Elements
            binding.fhTvScaleType1.text="°F"
            binding.fhTvScaleType2.text="°F"

            mainTemp = ((mainTemp)*1.8)+32
            mainFeelsLike= ((mainFeelsLike)*1.8)+32

            binding.fhTvTemp.text = ""+mainTemp.toInt()
            binding.fhTvFeelsLike.text = "Feels like "+mainFeelsLike.toInt()



            for (day in dayslist){
                day.temp.min = ((day.temp.min * 1.8)+32)
                day.temp.max = ((day.temp.max * 1.8)+32)
            }
            adapter.changeStatus(scaleType)
            adapter.notifyDataSetChanged()

        }else{
            scaleType = "C"
            scaleManager.saveScale(scaleType)
            //UI Elements
            binding.fhTvScaleType1.text="°C"
            binding.fhTvScaleType2.text="°C"

            mainTemp = ((mainTemp)-32)*0.556
            mainFeelsLike= ((mainFeelsLike)-32)*0.556

            binding.fhTvTemp.text = ""+mainTemp.toInt()
            binding.fhTvFeelsLike.text = "Feels like "+mainFeelsLike.toInt()


            for (day in dayslist){
                day.temp.min = ((day.temp.min -32)*0.556)
                day.temp.max = ((day.temp.max -32)*0.556)
            }
            adapter.changeStatus(scaleType)
            adapter.notifyDataSetChanged()
        }

    }

    private fun bindObservers() {
        homeViewModel.weatherLiveData.observe(viewLifecycleOwner, Observer {

            //binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {

                    // Update UserView with Latest Data
                    if (it.data != null) {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            binding.fhTvDate.text =
                                "Updated at " + getFormattedDate(it.data.daily[0].dt) + " " + getFormattedTime(
                                    it.data.current.dt
                                )
                        } else {
                            binding.fhTvDate.text =
                                "Updated at " + getFormattedTime(it.data.current.dt)
                        }

                        mainTemp = it.data.current.temp
                        mainFeelsLike = it.data.current.feels_like

                        binding.fhImg.setImageResource(getImageFromUrl(it.data.current.weather[0].icon))
                        binding.fhTvTemp.text = "" + it.data.current.temp.toInt()
                        binding.fhTvMain.text = "" + it.data.current.weather.get(0).main
                        binding.fhTvFeelsLike.text = "Feels like " + it.data.current.feels_like
                        binding.fhTvHumidity.text = "" + it.data.current.humidity
                        binding.fhTvWind.text = "" + it.data.current.wind_speed
                        binding.fhTvDewDrops.text = "" + it.data.current.dew_point

                        if (it.data.daily != null) {
                            dayslist = it.data.daily
                            adapter.submitList(it.data.daily)
                        }
                        if (scaleType=="F"){
                            scaleType="C"
                            changeUnit()
                        }

                        lifecycleScope.launch {
                            try{
                                var cityname:String = binding.fhTvName.text as String

                                if(dao.searchFavCities(cityname).size>0){
                                    isFav = true;
                                    binding.fhFav.setImageResource(R.drawable.icn_fav_filled)

                                }else{
                                    isFav = false;
                                    binding.fhFav.setImageResource(R.drawable.icn_fav_empty)
                                }
                               // Toast.makeText(activity,"Report\n"+"Name : "+cityname+"\n Result : "+dao.searchFavCities(cityname).size,Toast.LENGTH_SHORT).show()
                            }
                            catch (e:Exception){
                                Toast.makeText(activity,"Exception : "+e.message,Toast.LENGTH_LONG).show()
                            }
                        }

                    }
                }
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                is NetworkResult.Loading -> {
                    //binding.progressBar.isVisible = true
                }
            }


        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {


                        val place = Autocomplete.getPlaceFromIntent(data)
                        val myCoordinates: LatLng = place.latLng
                        homeViewModel.getWeather(
                            "" + myCoordinates.latitude,
                            "" + myCoordinates.longitude
                        )

                        lat=myCoordinates.longitude.toString()
                        lon = myCoordinates.longitude.toString()
                        binding.fhTvName.text = place.name


                        //val queriedLocation: LatLng = place.getLatLng()
                        //Log.i(TAG, "Place: ${place.name}, ${place.id}")
                        //Toast.makeText(activity,"Sent Data\nLat"+myCoordinates.latitude+" , Long"+myCoordinates.longitude+"\nOverall Data : "+place.latLng,Toast.LENGTH_SHORT).show()
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        //Log.i(TAG, status.statusMessage ?: "")
                        Toast.makeText(
                            activity,
                            "Error : " + status.statusMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
        /*  super.onActivityResult(requestCode, resultCode, data)
          Toast.makeText(activity,"Test",Toast.LENGTH_SHORT).show()*/
    }

    //region [Location Access]

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {

                        val geocoder = Geocoder(activity, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        lat = location.latitude.toString()
                        lon = location.longitude.toString()
                        //Toast.makeText(activity,"Lat : "+list[0].latitude+"\nLong : "+list[0].longitude+"\nLocality : "+list[0].locality+"\nAddress : "+list[0].getAddressLine(0),Toast.LENGTH_LONG).show()

                        // Call Current Location API
                        homeViewModel.getWeather("" + location.latitude, "" + location.longitude)
                        binding.fhTvName.text = list[0].locality


                    }
                }
            } else {
                Toast.makeText(activity, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (activity?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    //endregion

}