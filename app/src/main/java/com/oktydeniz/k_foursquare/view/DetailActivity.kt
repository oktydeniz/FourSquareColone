package com.oktydeniz.k_foursquare.view

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.oktydeniz.k_foursquare.R
import com.oktydeniz.k_foursquare.databinding.ActivityDetailBinding
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseQuery

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private var chosenPlaces = ""
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        chosenPlaces = intent.getStringExtra("name").toString()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        val locations = ParseQuery<ParseObject>("Locations")
        locations.whereEqualTo("name", chosenPlaces)
        locations.findInBackground { objects, e ->
            if (e != null) {
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
            } else {
                if (objects.size > 0) {
                    for (pObject in objects) {
                        val image = pObject.get("image") as ParseFile
                        image.getDataInBackground { data, ee ->
                            if (ee != null) {
                                Toast.makeText(
                                    applicationContext,
                                    ee.localizedMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                                binding.imageView.setImageBitmap(bitmap)
                                val name = pObject.getString("name")
                                val latitude = pObject.getString("latitude")
                                val longitude = pObject.getString("longitude")
                                val type = pObject.getString("type")
                                val atmosphere = pObject.getString("atmosphere")
                                binding.atmosphereDetail.text = atmosphere
                                binding.typeDetail.text = type
                                binding.nameDetail.text = name

                                val locationLatitude = latitude.toDouble()
                                val locationLongitude = longitude.toDouble()
                                val location = LatLng(locationLatitude, locationLongitude)
                                mMap.addMarker(MarkerOptions().title(name).position(location))
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
                            }
                        }
                    }
                }
            }
        }
    }
}