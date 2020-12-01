package com.example.pm_22689

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.pm_22689.api.EndPoints
import com.example.pm_22689.api.Marker
import com.example.pm_22689.api.ServiceBuilder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1
    private var locationPermissionGranted = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var markers: List<Marker>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
        // Lê das Shared Prefs se o utilizador já fez login e se não, inícia a atividade do login
        if (!sharedPref.getBoolean(getString(R.string.loggedin), false)) {
            val intentlogin = Intent(this, LoginActivity::class.java)
            intentlogin.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intentlogin.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intentlogin)
        } else {

            setContentView(R.layout.activity_maps)
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            val mapFragment =
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val call = request.getMarkers()
            var position: LatLng
            call.enqueue(object : Callback<List<Marker>> {
                override fun onResponse(
                    call: Call<List<Marker>>,
                    response: Response<List<Marker>>
                ) {
                    if (response.isSuccessful) {
                        markers = response.body()!!
                        Log.d("****GET", "$markers")
                        for (marker in markers) {
                            position = LatLng(marker.latitude.toString().toDouble(), marker.longitude.toString().toDouble())
                            if (marker.idUser == sharedPref.getInt("id", 0)) {
                                map.addMarker(
                                    MarkerOptions()
                                        .position(position)
                                        .title(marker.tipo)
                                        .icon( // para mudar a cor do marker para azul
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_BLUE
                                            )
                                        )
                                )
                            } else {
                                map.addMarker(
                                    MarkerOptions()
                                        .position(position)
                                        .title(marker.tipo)
                                    )
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<Marker>>, t: Throwable) {
                    Toast.makeText(this@MapsActivity, "Something went wrong...", Toast.LENGTH_LONG)
                        .show()
                    Log.d("****MAP", "failure")
                }
            })
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val latitude = 41.694200468850426
        val longitude = -8.846512287814242
        val homeLatLng = LatLng(latitude, longitude)

        /**
         * Zoom level:
         *  1: World
         *  5: Landmass/continent
         * 10: City
         * 15: Streets
         * 20: Buildings
         */
        val zoomLevel = 15f
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
        //map.addMarker(MarkerOptions().position(homeLatLng))

        setMapLongClick(map)
        enableMyLocation()
        //setPoiClick(map)
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            // A snippet is additional text that's displayed after the title.
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)) // para mudar a cor do marker para azul
            )
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker.showInfoWindow()
        }
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            return
        }
        map.isMyLocationEnabled = true              // enables the blue dot representing the user location (My Location Layer)
        locationPermissionGranted = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.logout -> {
            val sharedPref =
                getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean(getString(com.example.pm_22689.R.string.loggedin), false)
                commit()
            }
            Log.d("****SHAREDPREF", "Login pref changed to false")
            val intentlogin = Intent(this, LoginActivity::class.java)
            startActivity(intentlogin)
            true
        }
        R.id.mapnotes -> {
            val intentnotes = Intent(this, NotesActivity::class.java)
            startActivity(intentnotes)
            true
        }
        R.id.addMarker -> {
            markCurrentLocation()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun markCurrentLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         * https://github.com/googlemaps/android-samples/blob/29ca74b9a3894121f179b9f36b0a51755e7231b0/tutorials/kotlin/CurrentPlaceDetailsOnMap/app/src/main/java/com/example/currentplacedetailsonmap/MapsActivityCurrentPlace.kt#L193-L222
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        var location = task.result
                        if (location != null) {
                            map.addMarker(
                                MarkerOptions()
                                    .position(LatLng(location.latitude,location.longitude))
                                    .title(getString(R.string.dropped_pin))
                                    .icon( // para mudar a cor do marker para azul
                                        BitmapDescriptorFactory.defaultMarker(
                                            BitmapDescriptorFactory.HUE_BLUE
                                        )
                                    )
                            )
                            saveMarker(location.latitude,location.longitude,getString(R.string.dropped_pin))
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun saveMarker(latitude: Double, longitude: Double, tipo: String) {
        val sharedPref =
            getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
        var id = sharedPref.getInt("id", 0)

        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.saveMarker(id,latitude.toString(),longitude.toString(),tipo)
        var position: LatLng
        call.enqueue(object : Callback<Marker> {
            override fun onResponse(
                call: Call<Marker>,
                response: Response<Marker>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MapsActivity,"Marker saved",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Marker>, t: Throwable) {
                Toast.makeText(this@MapsActivity,"Marker not saved",Toast.LENGTH_SHORT).show()
            }
        })
    }
}