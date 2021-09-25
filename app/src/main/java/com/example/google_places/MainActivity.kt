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
        fragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.PHOTO_METADATAS,
                Place.Field.LAT_LNG
            )
        )

        if (!Places.isInitialized())
            Places.initialize(this, getString(R.string.api_key))

        val placesClient = Places.createClient(this)

        fragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(pl: Place) {
                val photoRequest = FetchPhotoRequest.builder(
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

            override fun onError(st: Status) {
                Toast.makeText(this@MainActivity, st.statusMessage, Toast.LENGTH_LONG).show()
            }
        })
    }
}