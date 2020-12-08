package com.example.pm_22689

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.pm_22689.api.*
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
    private val REQUEST_LOCATION_PERMISSION = 1
    private val newMarkerActivityRequestCode = 1
    private val updateMarkerActivityRequestCode = 2
    private val filterByDistance = 3
    private val filterByType = 4

    private lateinit var map: GoogleMap
    private var locationPermissionGranted = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var markers: List<Marker>
    private var deleteSelectedMarker = false
    private var tempMarker: com.google.android.gms.maps.model.Marker? = null


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
            call.enqueue(object : Callback<List<Marker>> {
                override fun onResponse(
                    call: Call<List<Marker>>,
                    response: Response<List<Marker>>
                ) {
                    if (response.isSuccessful) {
                        markers = response.body()!!
                        listAllMarkers()
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
        /**
         * Zoom level:
         *  1: World
         *  5: Landmass/continent
         * 10: City
         * 15: Streets
         * 20: Buildings
         */
        setMapLongClick(map)
        setMarkerClick(map)
        onInfoWindowLongClick(map)
        enableMyLocation()
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            val intentAddMarker = Intent(this@MapsActivity, MarkerDetails::class.java)
            intentAddMarker.putExtra("latitude", latLng.latitude.toString())
            intentAddMarker.putExtra("longitude", latLng.longitude.toString())
            startActivityForResult(intentAddMarker, newMarkerActivityRequestCode)
        }
    }

    private fun onInfoWindowLongClick(map: GoogleMap) {
        map.setOnInfoWindowLongClickListener {
            var splitTag: String = it.tag.toString()
            var tagID = splitTag.split("&").toTypedArray()
            if (tagID[0].toInt() == 0) {
                val intentAddMarker = Intent(this@MapsActivity, MarkerDetails::class.java)
                intentAddMarker.putExtra("latitude", it.position.latitude.toString())
                intentAddMarker.putExtra("longitude", it.position.longitude.toString())
                intentAddMarker.putExtra("descr", it.snippet)
                //it.remove()
                tempMarker = it
                startActivityForResult(intentAddMarker, newMarkerActivityRequestCode)
            } else {
                Toast.makeText(this, R.string.markerNotFromUser, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setMarkerClick(map: GoogleMap) {
        map.setOnMarkerClickListener { marker ->
            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
            } else {
                marker.showInfoWindow()
            }
            if (deleteSelectedMarker) {
                var splitTag: String = marker.tag.toString()
                var tagID = splitTag.split("&").toTypedArray()
                if (tagID[0].toInt() == 0) {
                    val request = ServiceBuilder.buildService(EndPoints::class.java)
                    val call = request.deleteMarker(tagID[1].toInt())
                    call.enqueue(object : Callback<ResponseDelete> {
                        override fun onResponse(
                            call: Call<ResponseDelete>,
                            response: Response<ResponseDelete>
                        ) {
                            if (response.isSuccessful) {
                                var resp: ResponseDelete = response.body()!!
                                if (resp.status) {
                                    marker.remove()
                                    Toast.makeText(
                                        this@MapsActivity,
                                        R.string.markerDeleted,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<ResponseDelete>, t: Throwable) {
                            Toast.makeText(
                                this@MapsActivity,
                                R.string.markerNotDeleted,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                } else {
                    Toast.makeText(this, R.string.markerNotFromUser, Toast.LENGTH_SHORT).show()
                }
                deleteSelectedMarker = false
            }
            true
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
        map.isMyLocationEnabled =
            true              // enables the blue dot representing the user location (My Location Layer)
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
                putBoolean(getString(R.string.loggedin), false)
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
        R.id.deleteMarker -> {
            deleteSelectedMarker = true
            Toast.makeText(this, R.string.selectMarkerToDelete, Toast.LENGTH_SHORT).show()
            true
        }
        R.id.mapDistanceFilter -> {
            val intentfilter = Intent(this, FilterActivity::class.java)
            startActivityForResult(intentfilter, filterByDistance)
            true
        }
        R.id.mapTypeFilter -> {
            val intentfilter = Intent(this, FilterActivity::class.java)
            intentfilter.putExtra("type", true)
            startActivityForResult(intentfilter, filterByType)
            true
        }
        R.id.resetFilter -> {
            listAllMarkers()
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

                            val intentAddMarker =
                                Intent(this@MapsActivity, MarkerDetails::class.java)
                            intentAddMarker.putExtra("latitude", location.latitude.toString())
                            intentAddMarker.putExtra("longitude", location.longitude.toString())
                            startActivityForResult(intentAddMarker, newMarkerActivityRequestCode)
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun saveMarker(
        latitude: Double,
        longitude: Double,
        tipo: String,
        marker: com.google.android.gms.maps.model.Marker,
        descr: String?
    ) {
        val sharedPref =
            getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
        var id = sharedPref.getInt("id", 0)

        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.saveMarker(id, latitude.toString(), longitude.toString(), tipo, descr)
        call.enqueue(object : Callback<Marker> {
            override fun onResponse(call: Call<Marker>, response: Response<Marker>) {
                if (response.isSuccessful) {
                    var resp = response.body()!!
                    Log.d("****saveMarker", "onResponse: ${resp.id}")
                    marker.tag = "0&${resp.id}"
                    markers.plus(resp)
                    Log.d("****TAG", "onResponse: $markers")
                    Toast.makeText(this@MapsActivity, R.string.markerSaved, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<Marker>, t: Throwable) {
                Log.d("****saveMarker", "onFailure: Não gravou na BD")
                Toast.makeText(this@MapsActivity, R.string.markerNotSaved, Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun updateMarker(
        latitude: Double,
        longitude: Double,
        tipo: String,
        marker: com.google.android.gms.maps.model.Marker,
        descr: String?
    ) {
        val sharedPref =
            getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
        var id = sharedPref.getInt("id", 0)

        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call =
            request.updateMarker(id, id, latitude.toString(), longitude.toString(), tipo, descr)
        call.enqueue(object : Callback<Marker> {
            override fun onResponse(call: Call<Marker>, response: Response<Marker>) {
                if (response.isSuccessful) {
                    var resp = response.body()!!
                    marker.tag = "0&${resp.id}"
                    Toast.makeText(this@MapsActivity, R.string.markerSaved, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<Marker>, t: Throwable) {
                Log.d("****updateMarker", "onFailure: Não gravou na BD")
                Toast.makeText(this@MapsActivity, R.string.markerNotSaved, Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {    //add note
            if (requestCode == newMarkerActivityRequestCode) {
                data?.getStringExtra(MarkerDetails.EXTRA_DATA_DESCR)?.let {
                    var latitude = data.getStringExtra(MarkerDetails.EXTRA_DATA_LAT)!!.toDouble()
                    var longitude = data.getStringExtra(MarkerDetails.EXTRA_DATA_LOG)!!.toDouble()
                    var tipo = data.getStringExtra(MarkerDetails.EXTRA_DATA_TIPO)
                    var descr = data.getStringExtra(MarkerDetails.EXTRA_DATA_DESCR)
                    Log.d("****passei aqui", "onActivityResult: $descr")
                    var userMarker: com.google.android.gms.maps.model.Marker
                    descr = if (!TextUtils.isEmpty(descr)) {
                        String.format(
                            Locale.getDefault(),
                            descr.toString()
                        )
                    } else {
                        String.format(
                            Locale.getDefault(),
                            "Lat: %1$.5f, Long: %2$.5f",
                            latitude.toFloat(),
                            longitude.toFloat()
                        )
                    }
                    if (tipo != null) {
                        tempMarker?.remove()
                        tempMarker = null
                        userMarker = map.addMarker(
                            MarkerOptions()
                                .position(LatLng(latitude, longitude))
                                .snippet(descr)
                                .icon( // para mudar a cor do marker para azul
                                    BitmapDescriptorFactory.defaultMarker(
                                        BitmapDescriptorFactory.HUE_BLUE
                                    )
                                )
                        )
                        userMarker.title = tipo
                        saveMarker(latitude, longitude, tipo, userMarker, descr)
                    }
                }
            } else if (requestCode == updateMarkerActivityRequestCode) {
                data?.getStringExtra(MarkerDetails.EXTRA_DATA_DESCR)?.let {
                    var latitude =
                        data.getStringExtra(MarkerDetails.EXTRA_DATA_LAT)!!.toDouble()
                    var longitude =
                        data.getStringExtra(MarkerDetails.EXTRA_DATA_LOG)!!.toDouble()
                    var tipo = data.getStringExtra(MarkerDetails.EXTRA_DATA_TIPO)
                    var descr = data.getStringExtra(MarkerDetails.EXTRA_DATA_DESCR)
                    Log.d("****passei aqui", "onActivityResult: $descr")
                    var userMarker: com.google.android.gms.maps.model.Marker
                    descr = if (!TextUtils.isEmpty(descr)) {
                        String.format(
                            Locale.getDefault(),
                            descr.toString()
                        )
                    } else {
                        String.format(
                            Locale.getDefault(),
                            "Lat: %1$.5f, Long: %2$.5f",
                            latitude.toFloat(),
                            longitude.toFloat()
                        )
                    }
                    if (tipo != null) {
                        tempMarker!!.title = tipo
                        tempMarker!!.snippet = descr
                        tempMarker!!.hideInfoWindow()
                        updateMarker(latitude, longitude, tipo, tempMarker!!, descr)
                    }
                }

            } else if (requestCode == filterByType){
                val reply = data?.getStringExtra("answer")
                filterByType(reply)
                
            } else if (requestCode == filterByDistance) {
                val reply = data?.getStringExtra("answer")?.toInt()
                filterByDistance(reply)
            }
        } else {
            Log.d("****onActivityResult", "Result code not OK")
            Toast.makeText(applicationContext, R.string.markerNotSaved, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun listAllMarkers(){
        val sharedPref = getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
        map.clear()
        for (marker in markers) {
            var position = LatLng(
                marker.latitude.toDouble(),
                marker.longitude.toDouble()
            )
            var snippet = if (marker.descricao != null) {
                String.format(
                    Locale.getDefault(),
                    marker.descricao.toString()
                )
            } else {
                String.format(
                    Locale.getDefault(),
                    "Lat: %1$.5f, Long: %2$.5f",
                    marker.latitude.toFloat(),
                    marker.longitude.toFloat()
                )
            }
            if (marker.idUser == sharedPref.getInt("id", 0)) {
                var userMarker = map.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(marker.tipo)
                        .snippet(snippet)
                        .icon( // para mudar a cor do marker para azul
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_BLUE
                            )
                        )
                )
                userMarker.tag = "0&${marker.id}"
            } else {
                var newMarker = map.addMarker(
                    MarkerOptions()
                        .position(position)
                        .snippet(snippet)
                        .title(marker.tipo)
                )
                newMarker.tag = "1&${marker.id}"
            }
        }
    }
    
    private fun filterByType(type: String?) = if(type.isNullOrBlank()) {
        Toast.makeText(this, "Tem que inserir um tipo", Toast.LENGTH_SHORT).show()
    } else {
        val sharedPref = getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
        map.clear()
        for (marker in markers) {
            var position = LatLng(
                marker.latitude.toDouble(),
                marker.longitude.toDouble()
            )
            var snippet = if (marker.descricao != null) {
                String.format(
                    Locale.getDefault(),
                    marker.descricao.toString()
                )
            } else {
                String.format(
                    Locale.getDefault(),
                    "Lat: %1$.5f, Long: %2$.5f",
                    marker.latitude.toFloat(),
                    marker.longitude.toFloat()
                )
            }
            if (marker.tipo == type) {
                var userMarker = map.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(marker.tipo)
                        .snippet(snippet)
                        .icon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN
                            )
                        )
                )
                if (marker.idUser == sharedPref.getInt("id", 0)) {
                    userMarker.tag = "0&${marker.id}"
                } else {
                    userMarker.tag = "1&${marker.id}"
                }
            }
        }
    }

    private fun filterByDistance(distance: Int?){
        val sharedPref = getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
        map.clear()
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        var location = task.result
                        if (location != null) {
                            for (marker in markers) {
                                var position = LatLng(
                                    marker.latitude.toDouble(),
                                    marker.longitude.toDouble()
                                )
                                var distanceToMarker = calculateDistance(location.latitude,position.latitude,location.longitude,position.longitude)
                                var snippet = if (marker.descricao != null) {
                                    String.format(
                                        Locale.getDefault(),
                                        "Distance: %1$.2f",
                                        distanceToMarker.toFloat()
                                    )
                                } else {
                                    String.format(
                                        Locale.getDefault(),
                                        "Distance: %1$.2f",
                                        distanceToMarker.toFloat()
                                    )
                                }
                                if (distanceToMarker < distance!!*1000) {
                                    var userMarker = map.addMarker(
                                        MarkerOptions()
                                            .position(position)
                                            .title(marker.tipo)
                                            .snippet(snippet)
                                            .icon(
                                                BitmapDescriptorFactory.defaultMarker(
                                                    BitmapDescriptorFactory.HUE_GREEN
                                                )
                                            )
                                    )
                                    if (marker.idUser == sharedPref.getInt("id", 0)) {
                                        userMarker.tag = "0&${marker.id}"
                                    } else {
                                        userMarker.tag = "1&${marker.id}"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun calculateDistance(lat1: Double, lat2: Double, lng1:Double, lng2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1,lng1,lat2,lng2, results)
        return results[0]
    }
}