package com.oktydeniz.k_foursquare.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.oktydeniz.k_foursquare.R
import com.oktydeniz.k_foursquare.util.Utils
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser
import java.io.ByteArrayOutputStream

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var latitute = ""
    private var longitute = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(listener)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = LocationListener { location ->
            val userLocation = LatLng(location.latitude, location.longitude)
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(userLocation).title("Your Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17f))

        }
        permissions()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_place -> {
                saveToParse()
            }
        }
        return super.onOptionsItemSelected(item)

    }

    private fun permissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2,
                2f,
                locationListener!!
            )
            mMap.clear()
            val lastLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val lastUserLocation = LatLng(lastLocation!!.latitude, lastLocation.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 17f))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty()) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    2,
                    2f,
                    locationListener!!
                )
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    private val listener = GoogleMap.OnMapLongClickListener { p0 ->
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0).title(Utils.placeName))
        latitute = p0.latitude.toString()
        longitute = p0.longitude.toString()
        Toast.makeText(this@MapsActivity, "Now Save This Place ", Toast.LENGTH_SHORT).show()
    }

    private fun saveToParse() {
        val byteArrayOutputStream = ByteArrayOutputStream()
        Utils.selectedImage?.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        val pFile = ParseFile("${Utils.placeName}.png", bytes)
        val parseObject = ParseObject("Locations")
        parseObject.put("name", Utils.placeName)
        parseObject.put("type", Utils.placeType)
        parseObject.put("atmosphere", Utils.atmosphereText)
        parseObject.put("latitude", latitute)
        parseObject.put("longitude", longitute)
        parseObject.put("user", ParseUser.getCurrentUser().username.toString())
        parseObject.put("image", pFile)
        parseObject.saveInBackground {
            if (it != null) {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Done !", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, StartingActivity::class.java))
                finish()
            }
        }

    }

}