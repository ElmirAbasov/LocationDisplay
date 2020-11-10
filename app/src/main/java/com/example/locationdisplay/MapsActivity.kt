package com.example.locationdisplay

import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    public lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        databaseRef = Firebase.database.reference

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        databaseRef.addValueEventListener(logListener)
    }

    val logListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            TODO("Could not read from database")
        }

        //     @SuppressLint("LongLogTag")
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                val locationlogging = dataSnapshot.child("userlocation").getValue(LocationLogging::class.java)
                var driverLat=locationlogging?.Latitude
                var driverLong=locationlogging?.Longitude


                val geocoder: Geocoder
                val addresses: List<Address>
                geocoder = Geocoder(applicationContext, Locale.getDefault())
                //Log.d("Latitude of driver", driverLat.toString())
                //    Log.d("Longitude read from database", driverLong.toString())

                if (driverLat !=null  && driverLong != null) {
                    addresses = geocoder.getFromLocation(driverLat, driverLong, 1)
                    val address: String = addresses[0].getAddressLine(0)
                    textViewUserLoc.text=address
                    //      textViewUserLoc.text="Latitude:"+driverLat.toString()+", Longitude: "+driverLong.toString()


                    val driverLoc = LatLng(driverLat, driverLong)
                    val markerOptions = MarkerOptions().position(driverLoc).title("Driver")
                    mMap.addMarker(markerOptions)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverLoc, 10f))
                    // 1: World, 5: Landmass/continent, 10: City, 15: Streets and 20: Buildings
                    Toast.makeText(
                        applicationContext,
                        "Locations accessed from the database",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}
