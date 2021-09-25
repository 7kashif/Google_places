package com.example.google_places

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.google_places.databinding.ActivityMainBinding
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragment =
            supportFragmentManager.findFragmentById(binding.autoCompleteFragment.id) as AutocompleteSupportFragment

        //here we set the fields we want to get
        fragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.PHOTO_METADATAS,
                Place.Field.LAT_LNG
            )
        )

        //we need to initialize places with our activity
        if (!Places.isInitialized())
            Places.initialize(this, getString(R.string.api_key))

        val placesClient = Places.createClient(this)

        //here we get the place we select from google auto complete fragment
        fragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(pl: Place) { //in case the place is successfully selected
                val photoRequest = FetchPhotoRequest.builder( //build a photo request to get first photo in the list of photos we get from response
                    Objects.requireNonNull(pl.photoMetadatas)[0]
                ).build()
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener { response ->
                    response?.let {
                        val bitmap = it.bitmap
                        binding.iv.setImageBitmap(bitmap)
                    }
                }.addOnFailureListener {
                    Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
                }
                Toast.makeText(this@MainActivity,pl.latLng.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onError(st: Status) { //in case any error occurred
                Toast.makeText(this@MainActivity, st.statusMessage, Toast.LENGTH_LONG).show()
            }
        })
    }
}